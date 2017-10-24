package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.idiot.HigherRankSameSuit
import domain.narcotic.{AllSameRank, ToLeftOf}
import org.combinators.solitaire.shared._

// domain
import domain._
import domain.ui._

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Score52 with Controller {

  object narcoticCodeGenerator {
    val generators = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, ToLeftOf] {
        case (registry:CodeGeneratorRegistry[Expression], c:ToLeftOf) => {
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          Java(s"""((org.combinators.solitaire.narcotic.Narcotic)game).toLeftOf($destination, $src)""").expression()
        }
      },

      CodeGeneratorRegistry[Expression, AllSameRank] {
        case (registry:CodeGeneratorRegistry[Expression], c:AllSameRank) => {
          Java(s"""((org.combinators.solitaire.narcotic.Narcotic)game).allSameRank()""").expression()
        }
      }
    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object NarcoticGenerator {
    def apply: CodeGeneratorRegistry[Expression] = narcoticCodeGenerator.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.narcotic").name()
    val semanticType: Type = packageName
  }

  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Narcotic").simpleName()
    val semanticType: Type = variationName
  }

  // Narcotic model derived from the domain model
  @combinator object NarcoticInitModel {

    // note: we could avoid passing in these parameters and just solely
    // visit the domain model. That is an alternative worth considering.

    def apply(): Seq[Statement] = {
      val deck = deckGenWithView("deck", "deckView")

      val pileGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldPiles", "fieldPileViews", "Pile")

      deck ++ pileGen
    }

    val semanticType: Type = game(game.model)
  }

  // generic deal cards from deck into the tableau
  @combinator object NarcoticInitLayout {
    def apply(): Seq[Statement] = Seq.empty

    val semanticType: Type = game(game.deal)
  }

  @combinator object NarcoticInitView {
    def apply(): Seq[Statement] = {

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      // start by constructing the DeckView
      var stmts = Java("deckView = new DeckView(deck);").statements()

      stmts = stmts ++ layout_place_one(stock, Java("deckView").name())
      stmts = stmts ++ layout_place_it(tableau, Java("fieldPileViews").name())

      stmts
    }

    val semanticType: Type = game(game.view)
  }

  @combinator object NarcoticInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {

      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      val pilesetup = loopControllerGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldPileViews",  "PileController")

      // add controllers for the DeckView here...
      val decksetup = controllerGen("deckView", "DeckController")

      pilesetup ++ decksetup
    }

    val semanticType: Type = variationName =>: game(game.control)
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

    val semanticType: Type = game(game.methods)
  }

  // This maps the elements in the Solitaire domain model into actual java 
  // fields. Not really compositional.
  @combinator object ExtraFields {
    def apply(): Seq[FieldDeclaration] = {
      val fields =
        Java(s"""|IntegerView scoreView;
                 |IntegerView numLeftView;""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      val decks = deckFieldGen(stock)

      val fieldPiles = fieldGen("Pile",  tableau.size())

      decks ++ fields ++ fieldPiles
    }

    val semanticType: Type = game(game.fields)
  }

  /**
    * Need for helper
    */
  @combinator object HelperMethodsFreeCell {
    def apply(): Seq[MethodDeclaration] = Seq.empty

    val semanticType: Type = constraints(constraints.methods)
  }



}
