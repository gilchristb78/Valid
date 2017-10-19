package org.combinators.solitaire.freecell

import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.ImportDeclaration
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._

// domain
import domain._
import domain.ui._

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate {

  /**
    * Every solitaire variation exists within a designated Java package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.freecell").name()
    val semanticType: Type = 'RootPackage
  }

  /**
    * Each solitaire variation has a name.
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("FreeCell").simpleName()
    val semanticType: Type = 'NameOfTheGame
  }

  // FreeCell model derived from the domain model
  @combinator object FreeCellInitModel {

    // note: we could avoid passing in these parameters and just solely
    // visit the domain model. That is an alternative worth considering.

    def apply(): Seq[Statement] = {

      val dg = deckGen ("deck")
      val colGen = loopConstructGen(solitaire.getTableau, "fieldColumns", "fieldColumnViews", "Column")
      val resGen = loopConstructGen(solitaire.getReserve, "fieldFreePiles", "fieldFreePileViews", "FreePile")
      val foundGen = loopConstructGen(solitaire.getFoundation, "fieldHomePiles", "fieldHomePileViews", "HomePile")

      dg ++ colGen ++ resGen ++ foundGen
    }

    val semanticType: Type = 'Init ('Model)
  }

  @combinator object FreeCellInitView {
    def apply(): Seq[Statement] = {

      val found = solitaire.getFoundation
      val tableau = solitaire.getTableau
      val free = solitaire.getReserve
      val lay = solitaire.getLayout

      var stmts = Seq.empty[Statement]

      stmts = stmts ++ layout_place_many(lay, found, Layout.Foundation, Java("fieldHomePileViews").name(), 97)
      stmts = stmts ++ layout_place_many(lay, free, Layout.Reserve, Java("fieldFreePileViews").name(), 97)
      stmts = stmts ++ layout_place_many(lay, tableau, Layout.Tableau, Java("fieldColumnViews").name(), 13*97)

      stmts
    }

    val semanticType: Type = 'Init ('View)
  }

  @combinator object FreeCellInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {

      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      val colsetup = loopControllerGen(solitaire.getTableau, "fieldColumnViews", "ColumnController")
      val freesetup = loopControllerGen(solitaire.getReserve, "fieldFreePileViews", "FreePileController")
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
      val numColumns = tableau.size
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

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {

      val reserve = solitaire.getReserve.size()
      val tableau = solitaire.getTableau.size()
      val numFreePiles: IntegerLiteralExpr = Java(s"$reserve").expression()
      val numColumns: IntegerLiteralExpr = Java(s"$tableau").expression()

      val methods = java.ExtraMethods.render(numFreePiles, numColumns).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

      val solvableMoves = Java(
        s"""
           |public boolean validColumn(Column column) {
           |		return column.alternatingColors() && column.descending();
           |}
           |
           |public java.util.Enumeration<Move> availableMoves() {
           |			java.util.Vector<Move> v = new java.util.Vector<Move>();
           |
           |        // try to build card to foundation
           |        for (Column c : fieldColumns) {
           |            for (Pile p : fieldHomePiles) {
           |                PotentialBuildColumn pbc = new PotentialBuildColumn(c, p);
           |                if (pbc.valid(this)) {
           |                    v.add(pbc);
           |                }
           |            }
           |        }
           |        // try to move cards from free cell to foundation
           |        for (Pile s : fieldFreePiles) {
           |            for (Pile d : fieldHomePiles) {
           |                PotentialBuildFreePileCard pbfpc = new PotentialBuildFreePileCard(s, d);
           |                if (pbfpc.valid(this)) {
           |                    v.add(pbfpc);
           |                }
           |            }
           |        }
           |        // try to move any column of any size (from greatest to smallest), but
           |        // to avoid infinite cycles, only move if remaining column is smaller
           |        // than the destination.
           |        for (Column s : fieldColumns) {
           |            for (Column d : fieldColumns) {
           |                if (s != d) {
           |                    for (int i = s.count(); i > 0; i--) {
           |                        PotentialMoveColumn pmc = new PotentialMoveColumn(s, d, i);
           |                        if (pmc.valid(this)) {
           |                            if (s.count() - i < d.count()) {
           |                                v.add(pmc);
           |                            }
           |                        }
           |                    }
           |                }
           |            }
           |        }
           |        // move smallest facing up column card to a free pile
           |        Column lowest = null;
           |        for (Column s : fieldColumns) {
           |            if (s.count() > 0) {
           |                if (lowest == null) {
           |                    lowest = s;
           |                } else if (s.rank() < lowest.rank()) {
           |                    lowest = s;
           |                }
           |            }
           |        }
           |        if (lowest != null) {
           |	        for (Pile p : fieldFreePiles) {
           |	            if (p.count() == 0) {
           |	                PotentialPlaceColumn ppc = new PotentialPlaceColumn(lowest, p);
           |	                v.add(ppc);
           |	                break;
           |	            }
           |	        }
           |        }
           |
           |        return v.elements();
           |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])


      methods ++ solvableMoves

    }
    val semanticType: Type = 'ExtraMethods :&: 'Column ('Column, 'AutoMovesAvailable) :&: 'AvailableMoves
  }

//  @combinator object EmptyExtraMethods {
//    def apply(): Seq[MethodDeclaration] = Seq.empty
//    val semanticType: Type = 'ExtraMethodsBad
//  }


   @combinator object MakeHomePile extends ExtendModel("Pile", "HomePile", 'HomePileClass)
   @combinator object MakeFreePile extends ExtendModel("Pile", "FreePile", 'FreePileClass)
   @combinator object MakeHomePileView extends ExtendView("PileView", "HomePileView", "HomePile", 'HomePileViewClass)
   @combinator object MakeFreePileView extends ExtendView("PileView", "FreePileView", "FreePile", 'FreePileViewClass)

   @combinator object FullFoundation {
    def apply(): Seq[Statement] = {
      val found = solitaire.getFoundation

      val nc = found.size()
      Java(
        s"""
           |int count = 0;
           |for (HomePile p : fieldHomePiles) {
           |  count += p.count();
           |}
           |if (count == 52) { return true; }
           |""".stripMargin).statements()
    }
    val semanticType: Type = 'WinConditionChecking :&: 'NonEmptySeq
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

      val decks = deckGen(solitaire)   // note: DeckView not needed for FreeCell

      // HACK: eventually remove 1 of first 2 parameters
      val fieldFreePiles = fieldGen("FreePile", "FreePile", "FreePileView", reserve.size())
      val fieldHomePiles = fieldGen("HomePile", "HomePile", "HomePileView", found.size())
      val fieldColumns = fieldGen("Column", "Column", "ColumnView", tableau.size())

      decks ++ fields ++ fieldFreePiles ++ fieldHomePiles ++ fieldColumns
    }

    val semanticType: Type = 'ExtraFields
  }
}
