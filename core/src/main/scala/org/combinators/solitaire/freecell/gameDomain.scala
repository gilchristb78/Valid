package org.combinators.solitaire.freecell

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._

// domain
import domain._

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class FreeCellDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Score52 {


  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.freecell").name()
    val semanticType: Type = 'RootPackage
  }

  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("FreeCell").simpleName()
    val semanticType: Type = 'NameOfTheGame
  }


  // FreeCell model derived from the domain model
  @combinator object FreeCellInitModel {

    // note: we could avoid passing in these parameters and just solely
    // visit the domain model. That is an alternative worth considering.

    def apply(): Seq[Statement] = {

      val NumFreePiles = solitaire.getReserve.size()
      val NumHomePiles = solitaire.getFoundation.size()
      val NumColumns = solitaire.getTableau.size()

      Java(
        s"""
           |// Basic start of pretty much any solitaire game that requires a deck.
           |deck = new Deck ("deck");
           |int seed = getSeed();
           |deck.create(seed);
           |addModelElement (deck);
           |
           | /* construct model elements */
           |for (int i = 0; i < $NumColumns; i++) {
           |  fieldColumns[i] = new Column(ColumnsPrefix + (i+1));
           |  addModelElement (fieldColumns[i]);
           |  fieldColumnViews[i] = new ColumnView(fieldColumns[i]);
           |}
           |
           |for (int i = 0; i < $NumFreePiles; i++) {
           |  fieldFreePiles[i] = new Pile (FreePilesPrefix + (i+1));
           |  addModelElement (fieldFreePiles[i]);
           |  fieldFreePileViews[i] = new PileView (fieldFreePiles[i]);
           |}
           |
           |for (int i = 0; i < $NumHomePiles; i++) {
           |  fieldHomePiles[i] = new Pile (HomePilesPrefix + (i+1));
           |  addModelElement (fieldHomePiles[i]);
           |  fieldHomePileViews[i] = new PileView(fieldHomePiles[i]);
           |}
           """.stripMargin).statements()
    }

    val semanticType: Type = 'Init ('Model)
  }
  @combinator object FreeCellInitView {
    def apply(): Seq[Statement] = {

      //val found = Solitaire.getInstance().getFoundation()
      val found = solitaire.getFoundation
      val tableau = solitaire.getTableau
      val NumColumns = tableau.size
      val free = solitaire.getReserve
      val lay = solitaire.getLayout
      val rectFound = lay.get(Layout.Foundation)
      val rectFree = lay.get(Layout.Reserve)
      val rectTableau = lay.get(Layout.Tableau)

      // (a) do the computations natively in scala to generate java code
      // (b) delegation to Layout class, but then needs to pull back into
      //     scala anyway
      //
      // card is 73 x 97

      var stmts = Seq.empty[Statement]

      // This could be a whole lot simpler! This places cards within Foundation rectangle
      // with card width of 97 cards each. Gap is fixed and determined by this function
      // Missing: Something that *maps* the domain model to 'fieldHomePileViews' and construction
      val it = lay.placements(Layout.Foundation, found, 97)
      var idx = 0
      while (it.hasNext) {
        val r = it.next()

        val s =
          Java(
            s"""
               |fieldHomePileViews[$idx].setBounds(${r.x}, ${r.y}, cw, ch);
               |addViewWidget(fieldHomePileViews[$idx]);
	       """.stripMargin).statements()

        idx = idx + 1
        stmts = stmts ++ s
      }

      // would be useful to have Scala utility for appending statements to single body.
      idx = 0
      while (idx < found.size()) {
        val xfree = rectFree.x + 15 * idx + idx * 73
        val s =
          Java(
            s"""
               |fieldFreePileViews[$idx].setBounds($xfree, 20, cw, ch);
               |addViewWidget(fieldFreePileViews[$idx]);
               """.stripMargin).statements()

        idx = idx + 1
        stmts = stmts ++ s
      }

      // now column placement
      idx = 0
      while (idx < tableau.size()) {
        val xtabl = rectTableau.x + 15 * idx + idx * 73
        val s =
          Java(
            s"""
               |fieldColumnViews[$idx].setBounds($xtabl, 40 + ch, cw, 13*ch);
               |addViewWidget(fieldColumnViews[$idx]);
               """.stripMargin).statements()

        idx = idx + 1
        stmts = stmts ++ s
      }

      stmts
    }

    val semanticType: Type = 'Init ('View)
  }

  @combinator object FreeCellInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {

      val nc = solitaire.getTableau.size()
      val np = solitaire.getFoundation.size()
      val nf = solitaire.getReserve.size()
      val name = NameOfGame.toString()

      Java(
        s"""
           |// setup controllers
           |for (int i = 0; i < $nc; i++) {
           |  fieldColumnViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
           |	fieldColumnViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
           |	fieldColumnViews[i].setMouseAdapter (new ${name}ColumnController (this, fieldColumnViews[i]));
           |}
           |for (int i = 0; i < $np; i++) {
           |  fieldHomePileViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
           |  fieldHomePileViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
           |  fieldHomePileViews[i].setMouseAdapter (new HomePileController (this, fieldHomePileViews[i]));
           |}
           |for (int i = 0; i < $nf; i++) {
           |  fieldFreePileViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
           |  fieldFreePileViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
           |  fieldFreePileViews[i].setMouseAdapter (new FreeCellPileController (this, fieldFreePileViews[i]));
           |}
           """.stripMargin).statements()

    }

    val semanticType: Type = 'NameOfTheGame =>: 'Init ('Control)
  }

  // generic deal cards from deck into the tableau
  @combinator object FreeCellInitLayout {
    def apply(): Seq[Statement] = {
      val tableau = solitaire.getTableau
      // standard logic to deal to all tableau cards
      var numColumns = tableau.size
      Java(
        s"""
           |int col = 0;
           |while (!deck.empty()) {
           |  fieldColumns[col++].add(deck.get());
           |  if (col >= $numColumns) {
           |    col = 0;
           |  }
           |}
           """.stripMargin).statements()
    }

    val semanticType: Type = 'Init ('Layout)
  }

  // create three separate blocks based on the domain model.
  @combinator object Initialization {
    def apply(minit: Seq[Statement],
      vinit: Seq[Statement],
      cinit: Seq[Statement],
      layout: Seq[Statement]): Seq[Statement] = {

      // @(ModelInit: Seq[Statement], ViewInit: Seq[Statement], ControlInit : Seq[Statement], SetupInitialState : Seq[Statement])
      java.DomainInit.render(minit, vinit, cinit, layout).statements()
    }
    val semanticType: Type = 'Init ('Model) =>: 'Init ('View) =>: 'Init ('Control) =>: 'Init ('Layout) =>: 'Initialization :&: 'NonEmptySeq
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

      val reserve = solitaire.getReserve.size()
      val tableau = solitaire.getTableau.size()
      val numFreePiles: IntegerLiteralExpr = Java(s"$reserve").expression()
      val numColumns: IntegerLiteralExpr = Java(s"$tableau").expression()

      java.ExtraMethods.render(numFreePiles, numColumns).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])
    }
    val semanticType: Type = 'ExtraMethods :&: 'Column ('FreeCellColumn, 'AutoMovesAvailable)
  }

  @combinator object EmptyExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty
    val semanticType: Type = 'ExtraMethodsBad
  }

  // This maps the elements in the Solitaire domain model into actual java fields.
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
          Java("MultiDeck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
        } else {
          Java("Deck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
        }

      val fieldFreePiles = java.FieldsTemplate
        .render("FreePile", Java("Pile").tpe(), Java("PileView").tpe(), Java(reserve.size().toString).expression())
        .classBodyDeclarations()
        .map(_.asInstanceOf[FieldDeclaration])

      val fieldHomePiles = java.FieldsTemplate
        .render("HomePile", Java("Pile").tpe(), Java("PileView").tpe(), Java(found.size().toString).expression())
        .classBodyDeclarations()
        .map(_.asInstanceOf[FieldDeclaration])

      val fieldColumns = java.FieldsTemplate
        .render("Column", Java("Column").tpe(), Java("ColumnView").tpe(), Java(tableau.size().toString).expression())
        .classBodyDeclarations()
        .map(_.asInstanceOf[FieldDeclaration])

      decks ++ fields ++ fieldFreePiles ++ fieldHomePiles ++ fieldColumns
    }

    val semanticType: Type = 'ExtraFields
  }
}
