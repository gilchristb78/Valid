package org.combinators.solitaire.idiot

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.constraints.movetypes.MoveComponents
import domain.idiot.HigherRankSameSuit
import org.combinators.solitaire.shared._

// domain
import domain._
import domain.ui._

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Score52 with Controller {

  object idiotCodeGenerator {
    val generators = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, HigherRankSameSuit] {
        case (registry:CodeGeneratorRegistry[Expression], c:HigherRankSameSuit) => {
          val cards = registry(c.cards)
          val src = registry(c.card)
          Java(s"""Solitaire.higher($cards,$src)""").expression()
        }
      }).merge(constraintCodeGenerators.generators)
  }

  /**
    * Every solitaire variation belongs in its own package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.idiot").name()
    val semanticType: Type = 'RootPackage
  }

  /**
    * Every solitaire variation has its own subclass with given name
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Idiot").simpleName()
    val semanticType: Type = 'NameOfTheGame
  }

  /**
    * Idiot has a deck and a collection of Columns.
    */
  @combinator object IdiotInitModel {

    def apply(): Seq[Statement] = {
      val deck = deckGenWithView("deck", "deckView")

      val colGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldColumns", "fieldColumnViews", "Column")

      deck ++ colGen
    }

    val semanticType: Type = 'Init ('Model)
  }

  /**
    * Layout properly positions deck in left and columns on right.
    */
  @combinator object IdiotInitView {
    def apply(): Seq[Statement] = {

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      // start by constructing the DeckView
      var stmts = Java("deckView = new DeckView(deck);").statements()

      // when placing a single element in Layout, use this API
      stmts = stmts ++ layout_place_it(stock, Java("deckView").name())
      stmts = stmts ++ layout_place_it(tableau, Java("fieldColumnViews").name())

      stmts
    }

    val semanticType: Type = 'Init ('View)
  }

  /**
    * Controllers are associated with each view widget by name.
    */
  @combinator object IdiotInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {
      val name = NameOfGame.toString()


      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      val colsetup = loopControllerGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldColumnViews", "ColumnController")

      // add controllers for the DeckView here...
      val decksetup = controllerGen("deckView", "DeckController")

      colsetup ++ decksetup
    }

    val semanticType: Type = 'NameOfTheGame =>: 'Init ('Control)
  }

  /**
    * No need to deal cards in Idiot; let player click on deck to deal first four.
    */
  @combinator object IdiotInitLayout {
    def apply(): Seq[Statement] = Seq.empty

    val semanticType: Type = 'Init ('InitialDeal)
  }


  /**
    * Vagaries of java imports means these must be defined as well.
    */
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = 'RootPackage =>: 'ExtraImports
  }

  /**
    * In Idiot, need to be able to tell if there exists a column (other than from) which contains
    * a top-facing card of same suit and higher rank than from.
    *
    * Note the SemanticType of this combinator has been specialized to include 'AvailableMoves for Solvable
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {
      Java(s"""|public boolean isHigher(Column from) {
               |  // empty columns are not eligible.
               |  if (from.empty()) { return false; }
               |  if (from.rank() == Card.ACE) { return false; }
               |  for (int i = 0; i < fieldColumns.length; i++) {
               |    // skip 'from' column and empty ones
               |    if (fieldColumns[i] == from || fieldColumns[i].empty()) continue;
               |    // must be same suit
               |    if (fieldColumns[i].suit() != from.suit()) continue;
               |    // if the current column (has same suit) and has
               |    // higher rank than the from column, we can remove.
               |    // Note ACES handles specially.
               |    if (fieldColumns[i].rank() > from.rank() || fieldColumns[i].rank() == Card.ACE) {
               |	    return true;
               |    }
               |  }
               |  return false;
               |}
               |
               |// Available moves based on this variation. Note this was hard-coded in generated code
               |// and then manually moved into this combinator.
               |public java.util.Enumeration<Move> availableMoves() {
               |		java.util.Vector<Move> v = new java.util.Vector<Move>();
               |
               |		// try all column moves
               |		for (int i = 0; i < fieldColumns.length; i++) {
               |			if (isHigher(fieldColumns[i])) {
               |				RemoveCard rc = new RemoveCard(fieldColumns[i]);
               |				if (rc.valid(this)) {
               |					v.add(rc);
               |				}
               |			}
               |		}
               |
               |		// try moving from a column just to an empty space; if one exists, move highest card
               |		// that has more than one card in the column
               |		Column emptyColumn = null;
               |		int maxRank = 0;
               |		int maxIdx = -1;
               |		for (int i = 0; i < fieldColumns.length; i++) {
               |			if (fieldColumns[i].empty()) {
               |				emptyColumn = fieldColumns[i];
               |			} else {
               |				if (fieldColumns[i].rank() > maxRank && fieldColumns[i].count() > 1) {
               |					maxRank = fieldColumns[i].rank();
               |					maxIdx = i;
               |				}
               |			}
               |		}
               |		if (emptyColumn != null && maxIdx >= 0) {
               |			// find column with highest rank, and try to move it.
               |			PotentialMoveCard mc = new PotentialMoveCard(fieldColumns[maxIdx], emptyColumn);
               |			if (mc.valid(this)) {
               |				v.add(mc);
               |			}
               |		}
               |
               |		// finally, request to deal four
               |		if (!this.deck.empty()) {
               |			DealDeck dd = new DealDeck(deck, fieldColumns);
               |			if (dd.valid(this)) {
               |				v.add(dd);
               |			}
               |		}
               |		return v.elements();
               |	}
               |""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

    }

    val semanticType: Type = 'ExtraMethods :&: 'AvailableMoves
  }

  /**
    * Create the necessary fields, including ScoreView and NumLeftView
    */
  @combinator object ExtraFields {
    def apply(): Seq[FieldDeclaration] = {
      val fields =
        Java(
          s"""
             |IntegerView scoreView;
             |IntegerView numLeftView;
             """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)
      val fieldColumns = fieldGen("Column", tableau.size())
      val decks = deckFieldGen(stock)

      decks ++ fields ++ fieldColumns
    }

    val semanticType: Type = 'ExtraFields
  }
}
