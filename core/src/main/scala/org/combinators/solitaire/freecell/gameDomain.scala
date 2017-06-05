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

  /** Field Declarations. */
  def fieldGen(name:String, modelType:String, viewType:String, num:Int):Seq[FieldDeclaration] = {
      Java(s"""
	     |protected static final String field${name}sPrefix = "$name";
	     |public $modelType[] field${name}s = new $modelType[$num];
	     |protected $viewType[] field${name}Views = new $viewType[$num];
             |""".stripMargin)
             .classBodyDeclarations()
             .map(_.asInstanceOf[FieldDeclaration])
  }

  /** Useful for constructing controller initializations. */
  def loopControllerGen(cont: Container, viewName : String, contName:String): Seq[Statement] = {
        val nc = cont.size()
        Java(
        s"""
           |for (int j = 0; j < $nc; j++) {
           |  $viewName[j].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
           |  $viewName[j].setUndoAdapter (new SolitaireUndoAdapter (this));
           |  $viewName[j].setMouseAdapter (new $contName (this, $viewName[j]));
           |}""".stripMargin).statements()
     }

  /** Ueful for constructing view initializations. */
  def loopConstructGen(cont: Container, modelName: String, viewName : String, typ:String): Seq[Statement] = {
        val nc = cont.size()
        Java(
        s"""
           |for (int j = 0; j < $nc; j++) {
           |  $modelName[j] = new $typ(${modelName}Prefix + (j+1));
           |  addModelElement ($modelName[j]);
           |  $viewName[j] = new ${typ}View($modelName[j]);
           |}""".stripMargin).statements()
     }

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

      val head = Java(
        s"""
           |// Basic start of pretty much any solitaire game that requires a deck.
           |deck = new Deck ("deck");
           |int seed = getSeed();
           |deck.create(seed);
           |addModelElement (deck);
           |""".stripMargin).statements()

      val colGen = loopConstructGen(solitaire.getTableau(), "fieldColumns", "fieldColumnViews", "Column")
      val resGen = loopConstructGen(solitaire.getReserve(), "fieldFreePiles", "fieldFreePileViews", "Pile")
      val foundGen = loopConstructGen(solitaire.getFoundation(), "fieldHomePiles", "fieldHomePileViews", "Pile")

      head ++ colGen ++ resGen ++ foundGen
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

      val colsetup = loopControllerGen(solitaire.getTableau, "fieldColumnViews", name + "ColumnController")
      val freesetup = loopControllerGen(solitaire.getReserve, "fieldFreePileViews", "FreeCellPileController")
      val homesetup = loopControllerGen(solitaire.getFoundation, "fieldHomePileViews", "HomePileController")

       colsetup ++ freesetup ++ homesetup
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
    val semanticType: Type = 'ExtraMethods :&: 'Column ('Column, 'AutoMovesAvailable)   // FCC
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

      val fieldFreePiles = fieldGen("FreePile", "Pile", "PileView", reserve.size())
      val fieldHomePiles = fieldGen("HomePile", "Pile", "PileView", found.size())
      val fieldColumns = fieldGen("Column", "Column", "ColumnView", tableau.size())

      decks ++ fields ++ fieldFreePiles ++ fieldHomePiles ++ fieldColumns
    }

    val semanticType: Type = 'ExtraFields
  }
}
