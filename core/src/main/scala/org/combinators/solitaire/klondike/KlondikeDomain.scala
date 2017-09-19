package org.combinators.solitaire.klondike

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

/**
  * Define domain using Score52 since this is a single-deck solitaire game.
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class KlondikeDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire)
  with GameTemplate with Score52 with Controller {

  /**
    * Every solitaire variation belongs in its own package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.klondike").name()
    val semanticType: Type = 'RootPackage
  }

  /**
    * Every solitaire variation has its own subclass with given name
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Klondike").simpleName()
    val semanticType: Type = 'NameOfTheGame
  }

  @combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)
  @combinator object MakeWastePileView extends ExtendView("PileView", "WastePileView", "WastePile", 'WastePileViewClass)


  /**
    * Idiot has a deck and a collection of Columns.
    */
  @combinator object InitModel {

    def apply(): Seq[Statement] = {
      val deck = deckGenWithView("deck", "deckView")

      val colGen = loopConstructGen(solitaire.getTableau, "fieldColumns", "fieldColumnViews", "Column")
      val foundGen = loopConstructGen(solitaire.getFoundation, "fieldPiles", "fieldPileViews", "Pile")
      val wastePileGen = loopConstructGen(solitaire.getWaste, "fieldWastePiles", "fieldWastePileViews", "WastePile")

      deck ++ colGen ++ foundGen ++ wastePileGen
    }

    val semanticType: Type = 'Init ('Model)
  }

  /**
    * Layout properly positions deck in left and columns on right.
    */
  @combinator object InitView {
    def apply(): Seq[Statement] = {

      val tableau = solitaire.getTableau
      val stock = solitaire.getStock
      val waste = solitaire.getWaste
      val lay = solitaire.getLayout

      // start by constructing the DeckView

      // when placing a single element in Layout, use this API
      val ds = layout_place_one(lay, stock, Layout.Stock, Java("deckView").name(), 97)
      val ws = layout_place_one(lay, stock, Layout.WastePile, Java("fieldWastePileViews[0]").name(), 97)
      val cs = layout_place_many(lay, tableau, Layout.Tableau, Java("fieldColumnViews").name(), 13*97)

      ds ++ ws ++ cs
    }

    val semanticType: Type = 'Init ('View)
  }

  /**
    * Controllers are associated with each view widget by name.
    */
  @combinator object InitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {

      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      //val colsetup = loopControllerGen(solitaire.getTableau, "fieldColumnViews", "ColumnController")
      //val foundsetup = loopControllerGen(solitaire.getFoundation, "pileViews", "PileController")
      //val wastesetup = loopControllerGen(solitaire.getFoundation, "wastePileView", "WastePileController")

      // add controllers for the DeckView here...
      //val decksetup = controllerGen("deckView", "DeckController")

      //colsetup ++ decksetup ++ foundsetup ++ wastesetup
      Seq.empty
    }

    val semanticType: Type = 'NameOfTheGame =>: 'Init ('Control)
  }

  /**
    * Fill in eventually
    */
  @combinator object InitLayout {
    def apply(): Seq[Statement] = Seq.empty

    val semanticType: Type = 'Init ('InitialDeal)
  }

  /**
    * Vagaries of java imports means these must be defined as well.
    */
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {

      Seq.empty
    }
    val semanticType: Type = 'RootPackage =>: 'ExtraImports
  }

  /**
    * Eventually will add extra methods here...
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty

    val semanticType: Type = 'ExtraMethods
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


      val fieldColumns = fieldGen("Column", "Column", "ColumnView", solitaire.getTableau.size)
      val foundPiles = fieldGen("Pile", "Pile", "PileView", solitaire.getFoundation.size)
      val wastePiles = fieldGen("WastePile", "WastePile", "WastePileView", solitaire.getWaste.size)
      val decks = deckGen(solitaire)

      decks ++ fields ++ fieldColumns ++ wastePiles ++ foundPiles
    }

    val semanticType: Type = 'ExtraFields
  }
}
