package org.combinators.solitaire.klondike

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, constraintCodeGenerators, generateHelper}

// domain
import domain._

/**
  * Define domain using Score52 since this is a single-deck solitaire game.
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class KlondikeDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with SemanticTypes
  with GameTemplate with Score52 with Controller {

  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Every solitaire variation belongs in its own package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.klondike").name()
    val semanticType: Type = packageName
  }

  /**
    * Every solitaire variation has its own subclass with given name
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Klondike").simpleName()
    val semanticType: Type = variationName
  }

  @combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)
  @combinator object MakeWastePileView extends ExtendView("PileView", "WastePileView", "WastePile", 'WastePileViewClass)

  /**
    * Klondike elements should be extracted from the domain
    */
  @combinator object InitModel {

    def apply(): Seq[Statement] = {
      val deck = deckGenWithView("deck", "deckView", solitaire.containers.get(SolitaireContainerTypes.Stock))

      val colGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldBuildablePiles", "fieldBuildablePileViews", "BuildablePile")
      val foundGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Foundation), "fieldPiles", "fieldPileViews", "Pile")
      val wastePileGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Waste), "fieldWastePiles", "fieldWastePileViews", "WastePile")

      deck ++ colGen ++ foundGen ++ wastePileGen
    }

    val semanticType: Type = game(game.model)
  }

  /**
    * Layout properly positions deck in left and columns on right.
    */
  @combinator object InitView {
    def apply(): Seq[Statement] = {

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)
      val waste = solitaire.containers.get(SolitaireContainerTypes.Waste)
      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)


      // start by constructing the DeckView

      // when placing a single element in Layout, use this API
      val ds = layout_place_one(stock, Java("deckView").name())
      val ws = layout_place_it_expr(waste, Java("fieldWastePileViews[0]").expression())
      val fd = layout_place_it(found, Java("fieldPileViews").name())
      val cs = layout_place_it(tableau,  Java("fieldBuildablePileViews").name())

      ds ++ ws ++ cs ++ fd
    }

    val semanticType: Type = game(game.view)
  }

  /**
    * Controllers are associated with each view widget by name.
    */
  @combinator object InitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {

      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      val bpsetup = loopControllerGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldBuildablePileViews", "BuildablePileController")
      val foundsetup = loopControllerGen(solitaire.containers.get(SolitaireContainerTypes.Foundation), "fieldPileViews", "PileController")
      val wastesetup = loopControllerGen(solitaire.containers.get(SolitaireContainerTypes.Waste), "fieldWastePileViews", "WastePileController")

      // add controllers for the DeckView here...
      val decksetup = controllerGen("deckView", "DeckController")

      bpsetup ++ decksetup ++ foundsetup ++ wastesetup
    }

    val semanticType: Type = variationName =>: game(game.control)
  }

//  /**
//    * NO longer needed, given deal semantics.
//    */
//  @combinator object InitLayout {
//    def apply(): Seq[Statement] = {
//      Java(s"""// prepare game by dealing facedown cards to all columns, then one face up
//		    |for (int pileNum = 0; pileNum < 7; pileNum++) {
//			  |  for (int num = 0; num < pileNum; num++) {
//				|    Card c = deck.get();
//        |    c.setFaceUp (false);
//				|    fieldBuildablePiles[pileNum].add (c);
//			  |  }
//        |  // This one is face up.
//			  |  fieldBuildablePiles[pileNum].add (deck.get());
//		    |}""".stripMargin).statements()
//    }
//
//    val semanticType: Type = game(game.deal)
//  }

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
    * Contains the logic whether the given column is alternating colors and descending.
    * Useful for filtering the valid moves when pressing on a column.
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty

    val semanticType: Type = game(game.methods)
  }

  /**
    * Create the necessary fields, including ScoreView and NumLeftView
    */
  @combinator object ExtraFields {
    def apply(): Seq[FieldDeclaration] = {
      val fields =
        Java(s"""|IntegerView scoreView;
                 |IntegerView numLeftView;""".stripMargin).fieldDeclarations()

      val fieldBuildablePiles = fieldGen("BuildablePile", solitaire.containers.get(SolitaireContainerTypes.Tableau).size)
      val foundPiles = fieldGen("Pile", solitaire.containers.get(SolitaireContainerTypes.Foundation).size)
      val wastePiles = fieldGen("WastePile", solitaire.containers.get(SolitaireContainerTypes.Waste).size)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      val decks = deckFieldGen(stock)

      decks ++ fields ++ fieldBuildablePiles ++ wastePiles ++ foundPiles
    }

    val semanticType: Type = game(game.fields)
  }

  /** No helper methods for Klondike. */
  @combinator object HelperMethodsKlondike {
    def apply(): Seq[MethodDeclaration] = Seq(
      generateHelper.fieldAccessHelper("foundation", "fieldPiles"),
      generateHelper.fieldAccessHelper("tableau", "fieldBuildablePiles"),
      generateHelper.fieldAccessHelper("wastePile", "fieldWastePiles"),
    )

    val semanticType: Type = constraints(constraints.methods)
  }
}
