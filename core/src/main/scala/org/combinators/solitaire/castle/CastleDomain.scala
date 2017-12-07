package org.combinators.solitaire.castle

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.castle.SufficientFree
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}

// domain
import domain._

/**
  *
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class CastleDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire)
  with GameTemplate with Controller with SemanticTypes {

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
    def apply(): Seq[MethodDeclaration] = {
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
               |    // FILL IN...
               |
               |    return v.elements();
               |}""".stripMargin).methodDeclarations()
    }

    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }

}
