package org.combinators.solitaire.castle

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.castle.SufficientFree
import org.combinators.solitaire.shared._

// domain
import domain._


/**
  * Define domain using Score52 since this is a single-deck solitaire game.
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class CastleDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire)
  with GameTemplate with Score52 with Controller with SemanticTypes {

  object castleCodeGenerator {
    val generators = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, SufficientFree] {
        case (registry:CodeGeneratorRegistry[Expression], c:SufficientFree) => {
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          val column = registry(c.column).get
          val tableau = registry(c.tableau).get
          Java(s"""ConstraintHelper.sufficientFree($column, $src, $destination, $tableau)""").expression()
        }
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
    * Specialized methods to help out in processing constraints. Specifically,
    * these are meant to be generic, things like getTableua, getReserve()
    */
  @combinator object HelperMethodsCastle {
    def apply(): Seq[MethodDeclaration] = Seq (
      generateHelper.fieldAccessHelper("tableau", "fieldRows"),

      Java(s"""
              |public static boolean sufficientFree (Column column, Stack src, Stack destination, Stack[] tableau) {
              |	int numEmpty = 0;
              |	for (Stack s : tableau) {
              |		if (s.empty() && s != destination) numEmpty++;
              |	}
              |
              |	return column.count() <= 1 + numEmpty;
              |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration]).head,
    )

    val semanticType: Type = constraints(constraints.methods)
  }


  /**
    * Every solitaire variation belongs in its own package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.castle").name()
    val semanticType: Type = packageName
  }

  /**
    * Every solitaire variation has its own subclass with given name
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Castle").simpleName()
    val semanticType: Type = variationName
  }


  @combinator object MakeRow extends ExtendModel("Column", "Row", 'RowClass)



  def loopCustomConstructGen(cont: Container, modelName: String, viewName : String, typ:String, typView:String): Seq[Statement] = {
    val nc = cont.size()
    Java(
      s"""
         |for (int j = 0; j < $nc; j++) {
         |  $modelName[j] = new $typ(${modelName}Prefix + (j+1));
         |  addModelElement ($modelName[j]);
         |  $viewName[j] = new ${typView}($modelName[j]);
         |}""".stripMargin).statements()
  }

  /**
    * Idiot has a deck and a collection of Rows.
    */
  @combinator object InitModel {

    def apply(): Seq[Statement] = {
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)
      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val deck = deckGen("deck", stock)

      val foundGen = loopConstructGen(found, "fieldPiles", "fieldPileViews", "Pile")
      val colGen = loopConstructGen(tableau, "fieldRows", "fieldRowViews", "Row")

      val orient = Java(
        s"""
           |for (int j = 0; j < 8; j++) {
           |   if (j < 4) {
           |      fieldRowViews[j].setJustification(RowView.RIGHT);
           |    } else {
           |      fieldRowViews[j].setDirection(RowView.LEFT);
           |    }
           |}
         """.stripMargin).statements()

      deck ++ colGen ++ foundGen ++ orient
    }

    val semanticType: Type = game(game.model)
  }

  /**
    * Layout properly positions deck in left and rows on right.
    */
  @combinator object InitView {
    def apply(): Seq[Statement] = {

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)


      // start by constructing the DeckView

      // when placing a single element in Layout, use this API
      val fd = layout_place_it(found, Java("fieldPileViews").name())
      val cs = layout_place_it(tableau,  Java("fieldRowViews").name())

      cs ++ fd
    }

    val semanticType: Type = game(game.view)
  }

  /**
    * Controllers are associated with each view widget by name.
    */
  @combinator object InitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {
      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)

      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      val colsetup = loopControllerGen(tableau, "fieldRowViews", "RowController")
      val foundsetup = loopControllerGen(found, "fieldPileViews", "PileController")
      //val wastesetup = loopControllerGen(solitaire.getFoundation, "wastePileView", "WastePileController")

      // add controllers for the DeckView here...
      //val decksetup = controllerGen("deckView", "DeckController")

      colsetup ++ foundsetup // ++ wastesetup
    }

    val semanticType: Type = variationName =>: game(game.control)
  }

  /**
    * Create the initial deal of Castle.
    *
    * Start by dealing out all cards, six to each row BUT when find ace, place them in an empty
    * foundation.
    */
  @combinator object InitLayout {
    def apply(): Seq[Statement] = {
      Java(s"""
           |int found = 0;
           |int col = 0;
           |while (!deck.empty()) {
           |   Card c = deck.get();
           |
           |   if (c.isAce()) {
           |     fieldPiles[found++].add(c);
           |   } else {
           |      fieldRows[col].add(c);
           |      if (fieldRows[col].count() == 6) {
           |         col++;
           |      }
           |   }
           |}
            """.stripMargin).statements()
    }

    val semanticType: Type = game(game.deal)
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
               |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])
    }

    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }

  /**
    * Create the necessary fields, including ScoreView and NumLeftView
    */
  @combinator object ExtraFields {
    def apply(): Seq[FieldDeclaration] = {
      val fields =
        Java(s"""|IntegerView scoreView;
                 |IntegerView numLeftView;""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      val fieldRows = fieldGen("Row", tableau.size)
      val foundPiles = fieldGen("Pile", found.size)
      val decks = deckFieldGen(stock)

      decks ++ fields ++ fieldRows ++ foundPiles
    }

    val semanticType: Type = game(game.fields)
  }

}
