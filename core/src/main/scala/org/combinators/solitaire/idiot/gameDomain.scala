package org.combinators.solitaire.idiot

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared

// domain
import domain._
import domain.ui._

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class IdiotDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Score52 with Controller {

  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.idiot").name()
    val semanticType: Type = 'RootPackage
  }

  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Idiot").simpleName()
    val semanticType: Type = 'NameOfTheGame
  }

  // Idiot model derived from the domain model
  @combinator object IdiotInitModel {

    // note: we could avoid passing in these parameters and just solely
    // visit the domain model. That is an alternative worth considering.

    def apply(): Seq[Statement] = {
      val deck = deckGen("deck", "deckView")

      val colGen = loopConstructGen(solitaire.getTableau(), "fieldColumns", "fieldColumnViews", "Column")

      deck ++ colGen 
    }

    val semanticType: Type = 'Init ('Model)
  }

  @combinator object IdiotInitView {
    def apply(): Seq[Statement] = {

      val tableau = solitaire.getTableau
      val stock = solitaire.getStock
      val lay = solitaire.getLayout

      var stmts = Seq.empty[Statement]
      
      val itd = lay.placements(Layout.Stock, stock, 97)
      val r = itd.next()

      val s = Java(s"""
               |deckView.setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
               |addViewWidget(deckView);
               """.stripMargin).statements()
      stmts = stmts ++ s


      // this can all be retrieved from the solitaire domain model by 
      // checking if a tableau is present, then do the following, etc... for others
      val itt = lay.placements(Layout.Tableau, tableau, 13*97)
      var idx = 0
      while (itt.hasNext) {
        val r = itt.next()

        val s = Java(s"""
               |fieldColumnViews[$idx].setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
               |addViewWidget(fieldColumnViews[$idx]);
               """.stripMargin).statements()

        idx = idx + 1
        stmts = stmts ++ s
      }

      stmts
    }

    val semanticType: Type = 'Init ('View)
  }

  @combinator object IdiotInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {
      val name = NameOfGame.toString()


      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      val colsetup = loopControllerGen(solitaire.getTableau, "fieldColumnViews", name + "ColumnController")

      // add controllers for the DeckView here...
      val decksetup = controllerGen("deckView", "DeckController")

       colsetup ++ decksetup
    }

    val semanticType: Type = 'NameOfTheGame =>: 'Init ('Control)
  }

  // generic deal cards from deck into the tableau
  @combinator object IdiotInitLayout {
    def apply(): Seq[Statement] = Seq.empty

    val semanticType: Type = 'Init ('Layout)
  }

  // create three separate blocks based on the domain model.
//  @combinator object Initialization {
//    def apply(minit: Seq[Statement],
//      vinit: Seq[Statement],
//      cinit: Seq[Statement],
//      layout: Seq[Statement]): Seq[Statement] = {
//
//      shared.java.DomainInit.render(minit, vinit, cinit, layout).statements()
//    }
//    val semanticType: Type = 'Init ('Model) =>: 'Init ('View) =>: 'Init ('Control) =>: 'Init ('Layout) =>: 'Initialization :&: 'NonEmptySeq
//  }

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
      Java(s"""|public boolean isHigher(Column from) {
               |  // empty columns are not eligible.
	       |  if (from.empty()) { return false; }
	       |  if (from.rank() == Card.ACE) { return false; }	
	       |  for (int i = 0; i < fieldColumns.length; i++) {
	       |    // skip 'from' column and empty ones
	       |    if (fieldColumns[i] == from ||
               |        fieldColumns[i].empty()) continue;
	       |    // must be same suit
	       |    if (fieldColumns[i].suit() != from.suit()) continue;
  	       |    // if the current column (has same suit) and has
               |    // higher rank than the from column, we can remove.
	       |    // Note ACES handles specially.
   	       |    if (fieldColumns[i].rank() > from.rank() || fieldColumns[i].rank() == Card.ACE) {
	       |	return true;
   	       |    }
	       |  }
               |  return false;
               |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

    }
    
    val semanticType: Type = 'ExtraMethods 
  }

  // This maps the elements in the Solitaire domain model into actual java 
  // fields. Not really compositional.
  @combinator object ExtraFields {
    def apply(): Seq[FieldDeclaration] = {
      val fields =
        Java(
          s"""
             |IntegerView scoreView;
             |IntegerView numLeftView;
             """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val found = solitaire.getFoundation
      val reserve = solitaire.getReserve
      val tableau = solitaire.getTableau
      val stock = solitaire.getStock

      val decks =
        if (stock.getNumDecks > 1) {
          Java("public MultiDeck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
        } else {
          Java("public Deck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
        }
      val deckViews = Java("DeckView deckView;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

      val fieldColumns = fieldGen("Column", "Column", "ColumnView", tableau.size())

      decks ++ fields ++ fieldColumns ++ deckViews
    }

    val semanticType: Type = 'ExtraFields
  }
}
