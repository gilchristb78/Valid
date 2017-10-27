package org.combinators.solitaire.castle

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.freecell.java
import org.combinators.solitaire.shared._

// domain
import domain._
import domain.ui._

/**
  * Define domain using Score52 since this is a single-deck solitaire game.
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class CastleDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire)
  with GameTemplate with Score52 with Controller {

  /**
    * Every solitaire variation belongs in its own package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.castle").name()
    val semanticType: Type = 'RootPackage
  }

  /**
    * Every solitaire variation has its own subclass with given name
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Castle").simpleName()
    val semanticType: Type = 'NameOfTheGame
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
      val deck = deckGen("deck")

      val foundGen = loopConstructGen(solitaire.getFoundation, "fieldPiles", "fieldPileViews", "Pile")
      val colGen = loopConstructGen(solitaire.getTableau, "fieldRows", "fieldRowViews", "Row")

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

    val semanticType: Type = 'Init ('Model)
  }

  /**
    * Layout properly positions deck in left and rows on right.
    */
  @combinator object InitView {
    def apply(): Seq[Statement] = {

      var stmts = Java("").statements()     // MUST BE SOME BETTER WAY OF GETTING EMPTY STATEMENTS

      // place ACEs piles
      for (idx <- 0 to 3) {
        val y = 110 * idx

        // aces
        stmts = stmts ++ Java(s"""
           |fieldPileViews[$idx].setBounds(240, $y, 73, 97);
           |addViewWidget(fieldPileViews[$idx]);
            """.stripMargin).statements()

        // rowviews on the left
        stmts = stmts ++ Java(s"""
           |fieldRowViews[$idx].setBounds(10, $y, 180, 97);
           |addViewWidget(fieldRowViews[$idx]);
          """.stripMargin).statements()


        // rowviews on the right
        val offset = idx + 4
        stmts = stmts ++ Java(s"""
           |fieldRowViews[$offset].setBounds(380, $y, 180, 97);
           |addViewWidget(fieldRowViews[$offset]);
        """.stripMargin).statements()
       }

      stmts
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
      val colsetup = loopControllerGen(solitaire.getTableau, "fieldRowViews", "RowController")
      val foundsetup = loopControllerGen(solitaire.getFoundation, "fieldPileViews", "PileController")
      //val wastesetup = loopControllerGen(solitaire.getFoundation, "wastePileView", "WastePileController")

      // add controllers for the DeckView here...
      //val decksetup = controllerGen("deckView", "DeckController")

      colsetup ++ foundsetup // ++ wastesetup
    }

    val semanticType: Type = 'NameOfTheGame =>: 'Init ('Control)
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

    val semanticType: Type = 'Init ('InitialDeal)
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
    val reserve = solitaire.getReserve.size()
    val tableau = solitaire.getTableau.size()
    val numFreePiles: IntegerLiteralExpr = Java(s"$reserve").expression()
    val numColumns: IntegerLiteralExpr = Java(s"$tableau").expression()

    java.ExtraMethods.render(numFreePiles, numColumns).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

    val semanticType: Type = 'ExtraMethods :&: 'AvailableMoves
  }

  /**
    * Create the necessary fields, including ScoreView and NumLeftView
    */
  @combinator object ExtraFields {
    def apply(): Seq[FieldDeclaration] = {
      val fields =
        Java(s"""|IntegerView scoreView;
                 |IntegerView numLeftView;""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val fieldRows = fieldGen("Row", "Row", "RowView", solitaire.getTableau.size)
      val foundPiles = fieldGen("Pile", "Pile", "PileView", solitaire.getFoundation.size)
      val decks = deckGen(solitaire)

      decks ++ fields ++ fieldRows ++ foundPiles
    }

    val semanticType: Type = 'ExtraFields
  }
}
