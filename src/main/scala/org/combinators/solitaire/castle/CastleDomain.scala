package org.combinators.solitaire.castle

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}

/**
  *
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class CastleDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire)
  with GameTemplate with Controller with SemanticTypes {

  // TODO:
  case class SufficientFree(src:MoveInformation, destination:MoveInformation, column:MoveInformation, tableau:MoveInformation) extends Constraint


  object castleCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, SufficientFree] {
        case (registry:CodeGeneratorRegistry[Expression], c:SufficientFree) =>
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          val column = registry(c.column).get
          val tableau = registry(c.tableau).get
          Java(s"""ConstraintHelper.sufficientFree($column, $src, $destination, $tableau)""").expression()
      },

    ).merge(constraintCodeGenerators.generators)
  }

  /** Each Solitaire variation must provide default do generation. */
  @combinator object DefaultDoGenerator {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = constraintCodeGenerators.doGenerators

    val semanticType: Type = constraints(constraints.do_generator)
  }

  /** Each Solitaire variation must provide default conversion for moves. */
  @combinator object DefaultUndoGenerator {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = constraintCodeGenerators.undoGenerators

    val semanticType: Type = constraints(constraints.undo_generator)
  }

  /**
    * Castle requires specialized extensions for constraints to work.
    */
  @combinator object CastleGenerator {
    def apply: CodeGeneratorRegistry[Expression] = castleCodeGenerator.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  /**
    * Specialized methods to help out in processing constraints. Specifically,
    * these are meant to be generic, things like getTableua, getReserve()
    */
  @combinator object  HelperMethodsCastle {
    def apply(): Seq[BodyDeclaration[_]] = {
      val methods = generateHelper.helpers(solitaire)

      methods ++ Java(
        s"""
           |public static boolean sufficientFree (Column column, Stack src, Stack destination, Stack[] tableau) {
           |	int numEmpty = 0;
           |	for (Stack s : tableau) {
           |		if (s.empty() && s != destination && s != src) numEmpty++;
           |	}
           |
           |	return column.count() <= 1 + numEmpty;
           |}""".stripMargin).methodDeclarations()
    }

    val semanticType: Type = constraints(constraints.methods)
  }



  @combinator object MakeRow extends ExtendModel("Column", "Row", 'RowClass)


  /**
    * Castle needs additional code to control the orientation of the RowView. I think
    * these could be moved into application domain and solution domain, but for now
    * this is the easiest way to just extend an existing combinator
    */
  @combinator object InitModel extends ProcessModel(solitaire:Solitaire) {

    override def apply: Seq[Statement] = super.apply ++
              Java(s"""
                   |for (int j = 0; j < 8; j++) {
                   |   if (j < 4) {
                   |      tableauView[j].setJustification(RowView.RIGHT);
                   |    } else {
                   |      tableauView[j].setDirection(RowView.LEFT);
                   |    }
                   |}
                 """.stripMargin).statements()
  }

  // vagaries of java imports means these must be defined as well.
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = packageName =>: game(game.imports)
  }

  /**
    * Eventually will add extra methods here...
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {
      Java (s"""public java.util.Enumeration<Move> availableMoves() {
               |		java.util.Vector<Move> v = new java.util.Vector<Move>();
               |
               |
               |        for (Row row : tableau) {
               |        	for (Pile foundationPile : foundation) {
               |        		PotentialBuildRow move = new PotentialBuildRow(row, foundationPile, 1);
               |        		if (move.valid(this)) {
               |        			v.add(move);
               |        		}
               |        	}
               |        }
               |
               |        for (Row row : tableau) {
               |        	if (row.empty()) { continue; }
               |        	for (Row second : tableau) {
               |
               |        		if (second != row) {
               |        			PotentialMoveRow move = new PotentialMoveRow(row, second);
               |
               |        			// Disallow moving a single card from a row of one card to an empty pile.
               |        			if (row.count() == 1 && second.empty()) { continue; }
               |
               |        			if (move.valid(this)) {
               |
               |        				// Be careful about like-to-like moves. That is, if there is a 6 Hearts/5 Clubs
               |            			// and you move the 5 to another 6. This is only meaningful if the 6 Hearts can
               |            			// be placed on the foundation directly.
               |            			if (row.count() > 1 && second.count() >= 1) {
               |            				Card peekedCard = row.peek(row.count() - 2);
               |            				if (peekedCard.getRank() == second.rank()) {
               |            					boolean canPlace = false;
               |            					for (Pile foundationPile : foundation) {
               |            						if ((foundationPile.suit() == peekedCard.getSuit()) && (foundationPile.rank() + 1 == peekedCard.getRank())) {
               |            							canPlace = true;
               |            						}
               |            					}
               |
               |            					if (!canPlace) { continue; }
               |            				}
               |            			}
               |
               |        				v.add(move);
               |        			}
               |        		}
               |        	}
               |        }
               |
               |    return v.elements();
               |}""".stripMargin).methodDeclarations()
    }

    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }

}
