package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._

// domain
import domain._
import domain.ui._

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Score52 with Controller {

  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.narcotic").name()
    val semanticType: Type = 'RootPackage
  }

  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Narcotic").simpleName()
    val semanticType: Type = 'NameOfTheGame
  }

  // Narcotic model derived from the domain model
  @combinator object NarcoticInitModel {

    // note: we could avoid passing in these parameters and just solely
    // visit the domain model. That is an alternative worth considering.

    def apply(): Seq[Statement] = {
      val deck = deckGen("deck")

      val pileGen = loopConstructGen(solitaire.getTableau, "fieldPiles", "fieldPileViews", "Pile")

      deck ++ pileGen 
    }

    val semanticType: Type = 'Init ('Model)
  }

   // generic deal cards from deck into the tableau
   @combinator object NarcoticInitLayout {
    def apply(): Seq[Statement] = Seq.empty

    val semanticType: Type = 'Init ('InitialDeal)
  }

  @combinator object NarcoticInitView {
    def apply(): Seq[Statement] = {

      val tableau = solitaire.getTableau
      val stock = solitaire.getStock
      val lay = solitaire.getLayout

      var stmts = Seq.empty[Statement]
      
      val itd = lay.placements(Layout.Stock, stock, 97)
      val r = itd.next()

      val s = Java(s"""
               |deckView = new DeckView(deck);
               |deckView.setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
               |addViewWidget(deckView);
               """.stripMargin).statements()
      stmts = stmts ++ s


      // this can all be retrieved from the solitaire domain model by 
      // checking if a tableau is present, then do the following, etc... for others
      val itt = lay.placements(Layout.Tableau, tableau, 97)
      var idx = 0
      while (itt.hasNext) {
        val r = itt.next()

        val s = Java(s"""
               |fieldPileViews[$idx].setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
               |addViewWidget(fieldPileViews[$idx]);
               """.stripMargin).statements()

        idx = idx + 1
        stmts = stmts ++ s
      }

      stmts
    }

    val semanticType: Type = 'Init ('View)
  }

  @combinator object NarcoticInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {


      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      val pilesetup = loopControllerGen(solitaire.getTableau, "fieldPileViews",  "PileController")

      // add controllers for the DeckView here...
      val decksetup = controllerGen("deckView", "DeckController")

      pilesetup ++ decksetup
    }

    val semanticType: Type = 'NameOfTheGame =>: 'Init ('Control)
  }

  // vagaries of java imports means these must be defined as well.
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = 'RootPackage =>: 'ExtraImports
  }

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {
       Java(s"""|public boolean toLeftOf(Stack target, Stack src) {
                |  // Check whether target is to left of src
                |  for (int i = 0; i < fieldPiles.length; i++) {
                |    if (fieldPiles[i] == target) {
                |      return true;   // found target first (in left-right)
                |    }
                |    if (fieldPiles[i] == src) {
                |      return false;  // found src first
                |    }
                |  }
                |  return false; // will never get here
                |}
                |
                | public boolean allSameRank() {
                |   if (fieldPiles[0].empty()) { return false; }
                |   // Check whether tops of all piles are same rank
                |   for (int i = 1; i < fieldPiles.length; i++) {
                |      if (fieldPiles[i].empty()) { return false; }
                |      if (fieldPiles[i].rank() != fieldPiles[i-1].rank()) {
                |        return false;
                |      }
                |   }
                |  // looks good
                |  return true;
                |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

    }
    
    val semanticType: Type = 'ExtraMethods 
  }

  // This maps the elements in the Solitaire domain model into actual java 
  // fields. Not really compositional.
  @combinator object ExtraFields {
    def apply(): Seq[FieldDeclaration] = {
      val fields =
        Java(s"""|IntegerView scoreView;
                 |IntegerView numLeftView;""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val tableau = solitaire.getTableau
      val stock = solitaire.getStock

      val decks =
        if (stock.getNumDecks > 1) {
          Java("public MultiDeck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
        } else {
          Java("public Deck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
        }
      val deckViews = Java("DeckView deckView;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val fieldPiles = fieldGen("Pile", "Pile", "PileView", tableau.size())

      decks ++ fields ++ fieldPiles ++ deckViews
    }

    val semanticType: Type = 'ExtraFields
  }
}
