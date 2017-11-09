package org.combinators.solitaire.bigforty
import domain._
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.bigforty.AllSameSuit
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, constraintCodeGenerators, generateHelper}




class gameDomain (override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with SemanticTypes
  with GameTemplate with Score52 with Controller {

  object bigFortyCodeGenerator {
    val generators = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, AllSameSuit] {
        case (registry: CodeGeneratorRegistry[Expression], c: AllSameSuit) => {
          Java(s"""((org.combinators.solitaire.bigforty.BigForty)game).allSameSuit()""").expression()
        }
      }
    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object bigFortyGenerator {
    def apply: CodeGeneratorRegistry[Expression] = bigFortyCodeGenerator.generators

    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Every solitaire variation belongs in its own package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.bigforty").name()

    val semanticType: Type = packageName
  }

  /**
    * Every solitaire variation has its own subclass with given name
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("BigForty").simpleName()

    val semanticType: Type = variationName
  }

  @combinator object HelperMethodsBigForty {
    def apply(): Seq[MethodDeclaration] = Seq.empty

    val semanticType: Type = constraints(constraints.methods)
  }

  @combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)

  @combinator object MakeWastePileView extends ExtendView("PileView", "WastePileView", "WastePile", 'WastePileViewClass)


  /**
    * BigForty has a deck and a collection of Columns.
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
      val cs = layout_place_it(tableau, Java("fieldBuildablePileViews").name())

      ds ++ ws ++ cs ++ fd
    }

    val semanticType: Type = game(game.view)
  }

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


  /**
    * Fill in eventually
    */
  @combinator object InitLayout {
    def apply(): Seq[Statement] = {
      Java(s"""// prepare game by dealing faceup cards to all columns,
              		    |for (int pileNum = 0; pileNum < 10; pileNum++) {
              			  |  for (int num = 0; num < 4; num++) {
              				|    Card c = deck.get();
              			  |  fieldBuildablePiles[pileNum].add (deck.get());}
              		    |}""".stripMargin).statements()
    }

    val semanticType:

      Type = game(game.deal)
  }
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").

          importDeclaration()
      )
    }
    val semanticType: Type = packageName =>: game(game.imports)
  }

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {Java(s"""
              |public boolean validColumn(Column column) {
              |		return column.AllSameSuit() && column.descending();
              |}
            """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])
    }

    val semanticType: Type = game(game.methods)
  }

  /**
    * Create the necessary fields, including ScoreView and NumLeftView
    */
  @combinator object ExtraFields {
    def apply(): Seq[FieldDeclaration] = {
      val fields = Java(s"""|IntegerView scoreView;
                            |IntegerView numLeftView;""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val fieldBuildablePiles = fieldGen("BuildablePile", solitaire.containers.get(SolitaireContainerTypes.Tableau).size)
      val foundPiles = fieldGen("Pile", solitaire.containers.get(SolitaireContainerTypes.Foundation).size)
      val wastePiles = fieldGen("WastePile", solitaire.containers.get(SolitaireContainerTypes.Waste).size)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      val decks = deckFieldGen(stock)

      decks ++ fields ++ fieldBuildablePiles ++ wastePiles ++ foundPiles
    }

    val semanticType: Type = game(game.fields)
  }
}

