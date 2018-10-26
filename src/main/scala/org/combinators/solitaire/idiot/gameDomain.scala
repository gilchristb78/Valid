package org.combinators.solitaire.idiot

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain.{Element, Solitaire}
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.{Controller, GameTemplate, SolitaireDomain}
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.shared.compilation.generateHelper

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {

  // override as needed in your own own specialized trait. I.e. "AcesUpPile" -> "PileView"
  override def baseViewNameFromElement(e: Element): String = super.baseViewNameFromElement(e)

  /**
    * Register the higher method which determines whether any other column in tableau has higher card, same suite
    */
  object idiotCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, HigherRankSameSuit] {
        case (registry:CodeGeneratorRegistry[Expression], HigherRankSameSuit(c)) =>

          // trust that the registry already has cases for
          // MoveInformation (i.e., Source, Destination) that before
          // had been ${mc.name} but are now typed (i.e., Source => "source")
          val src = registry(c).get
          Java(s"""ConstraintHelper.higher(game, $src)""").expression()

      }).merge(constraintCodeGenerators.generators)
  }

  @combinator object IdiotGenerator {
    def apply: CodeGeneratorRegistry[Expression] = idiotCodeGenerator.generators
    val semanticType: Type = constraints(constraints.generator)
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
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  /**
    * Vagaries of java imports means these must be defined as well.
    */
  @combinator object ExtraImports {
    def apply(stmts:Seq[Statement], nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = game(game.control) =>: packageName =>: game(game.imports)
  }

  /**
    * Note the SemanticType of this combinator has been specialized to include 'AvailableMoves for Solvable
    *
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {
      Java(s"""|// Available moves based on this variation.
               |public java.util.Enumeration<Move> availableMoves() {
               |		java.util.Vector<Move> v = new java.util.Vector<Move>();
               |
               |		// try all column moves
               |		for (int i = 0; i < tableau.length; i++) {
               |			RemoveCard rc = new RemoveCard(tableau[i]);
               |			if (rc.valid(this)) {
               |				v.add(rc);
               |		  }
               |		}
               |
               |		// try moving from a column just to an empty space; if one exists, move highest card
               |		// that has more than one card in the column
               |		Column emptyColumn = null;
               |		int maxRank = 0;
               |		int maxIdx = -1;
               |		for (int i = 0; i < tableau.length; i++) {
               |			if (tableau[i].empty()) {
               |				emptyColumn = tableau[i];
               |			} else {
               |				if (tableau[i].rank() > maxRank && tableau[i].count() > 1) {
               |					maxRank = tableau[i].rank();
               |					maxIdx = i;
               |				}
               |			}
               |		}
               |		if (emptyColumn != null && maxIdx >= 0) {
               |			// find column with highest rank, and try to move it.
               |			PotentialMoveCard mc = new PotentialMoveCard(tableau[maxIdx], emptyColumn);
               |			if (mc.valid(this)) {
               |				v.add(mc);
               |			}
               |		}
               |
               |		// finally, request to deal four
               |		if (!this.deck.empty()) {
               |			DealDeck dd = new DealDeck(deck, tableau);
               |			if (dd.valid(this)) {
               |				v.add(dd);
               |			}
               |		}
               |		return v.elements();
               |	}
               |""".stripMargin).methodDeclarations()

    }

    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }


  /** Idiot has logic to determine if any existing card on the tableau is higher in same suit. */
  @combinator object HelperMethodsIdiot {
    def apply(): Seq[BodyDeclaration[_]] = {
      val methods = generateHelper.helpers(solitaire)

      methods ++ Java(s"""
                         |public static boolean higher(Solitaire game, Stack source) {
                         |    // empty columns are not eligible.
                         |    if (source.empty()) {
                         |        return false;
                         |    }
                         |    if (source.rank() == Card.ACE) {
                         |        return false;
                         |    }
                         |
                         |    Stack[] tableau = tableau(game);
                         |    for (int i = 0; i < tableau(game).length; i++) {
                         |        // skip 'from' column and empty ones
                         |        if (tableau[i] == source || tableau[i].empty())
                         |            continue;
                         |        // must be same suit
                         |        if (tableau[i].suit() != source.suit())
                         |            continue;
                         |        // Note ACES handles specially.
                         |        if (tableau[i].rank() > source.rank() || tableau[i].rank() == Card.ACE) {
                         |            return true;
                         |        }
                         |    }
                         |    return false;
                         |}""".stripMargin).methodDeclarations()
    }

    val semanticType: Type = constraints(constraints.methods)
  }
}
