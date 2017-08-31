package org.combinators.solitaire.freecell

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
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

//      val NumFreePiles = solitaire.getReserve.size()
//      val NumHomePiles = solitaire.getFoundation.size()
//      val NumColumns = solitaire.getTableau.size()

      val head = Java(
        s"""
           |// Basic start of pretty much any solitaire game that requires a deck.
           |deck = new Deck ("deck");
           |int seed = getSeed();
           |deck.create(seed);
           |addModelElement (deck);
           |""".stripMargin).statements()

      val colGen = loopConstructGen(solitaire.getTableau(), "fieldColumns", "fieldColumnViews", "Column")
      val resGen = loopConstructGen(solitaire.getReserve(), "fieldFreePiles", "fieldFreePileViews", "FreePile")
      val foundGen = loopConstructGen(solitaire.getFoundation(), "fieldHomePiles", "fieldHomePileViews", "HomePile")

      head ++ colGen ++ resGen ++ foundGen
    }

    val semanticType: Type = 'Init ('Model)
  }

  @combinator object FreeCellInitView {
    def apply(): Seq[Statement] = {

      //val found = Solitaire.getInstance().getFoundation()
      val found = solitaire.getFoundation
      val tableau = solitaire.getTableau
//      val NumColumns = tableau.size
      val free = solitaire.getReserve
      val lay = solitaire.getLayout

      var stmts = Seq.empty[Statement]

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

      val itr = lay.placements(Layout.Reserve, free, 97)
      idx = 0
      while (itr.hasNext) {
        val r = itr.next()

        val s =
          Java(
            s"""
               |fieldFreePileViews[$idx].setBounds(${r.x}, ${r.y}, cw, ch);
               |addViewWidget(fieldFreePileViews[$idx]);
               """.stripMargin).statements()

        idx = idx + 1
        stmts = stmts ++ s
      }

      val itt = lay.placements(Layout.Tableau, tableau, 13*97)
      idx = 0
      while (itt.hasNext) {
        val r = itt.next()

        val s =
          Java(
            s"""
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

  @combinator object FreeCellInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {

      val nc = solitaire.getTableau.size()
      val np = solitaire.getFoundation.size()
      val nf = solitaire.getReserve.size()
      val name = NameOfGame.toString()

      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
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

//  @combinator object EmptyExtraMethods {
//    def apply(): Seq[MethodDeclaration] = Seq.empty
//    val semanticType: Type = 'ExtraMethodsBad
//  }

  class ExtendModel(parent: String, subclass: String, typ:Symbol) {

    def apply(rootPackage: Name): CompilationUnit = {
       val name = rootPackage.toString()
       Java(s"""package $name;
                import ks.common.model.*;
                public class $subclass extends $parent {
		  public $subclass (String name) {
		    super(name);
		  }
		}
	     """).compilationUnit
    }

    val semanticType : Type = 'RootPackage =>: typ 
  }

   class ExtendView(parent: String, subclass: String, model: String, typ:Symbol) {

    def apply(rootPackage: Name): CompilationUnit = {
       val name = rootPackage.toString()
       Java(s"""package $name;
                import ks.common.view.*;
                public class $subclass extends $parent {
                  public $subclass ($model element) {
                    super(element);
                  }
                }
             """).compilationUnit
    }

    val semanticType : Type = 'RootPackage =>: typ
  }

   @combinator object MakeHomePile extends ExtendModel("Pile", "HomePile", 'HomePileClass)

   @combinator object MakeFreePile extends ExtendModel("Pile", "FreePile", 'FreePileClass)

   @combinator object MakeHomePileView extends ExtendView("PileView", "HomePileView", "HomePile", 'HomePileViewClass)

   @combinator object MakeFreePileView extends ExtendView("PileView", "FreePileView", "FreePile", 'FreePileViewClass)

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
          Java("MultiDeck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
        } else {
          Java("Deck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
        }

      // HACK: eventually remove 1 of first 2 parameters
      val fieldFreePiles = fieldGen("FreePile", "FreePile", "FreePileView", reserve.size())
      val fieldHomePiles = fieldGen("HomePile", "HomePile", "HomePileView", found.size())
      val fieldColumns = fieldGen("Column", "Column", "ColumnView", tableau.size())

      decks ++ fields ++ fieldFreePiles ++ fieldHomePiles ++ fieldColumns
    }

    val semanticType: Type = 'ExtraFields
  }
}
