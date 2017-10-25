package org.combinators.solitaire.freecell

import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, IntegerLiteralExpr, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.ImportDeclaration
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.freeCell.SufficientFree
import org.combinators.solitaire.shared._

// domain
import domain._

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with Controller with GameTemplate  {

  object freecellCodeGenerator {
    val generators = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, SufficientFree] {
        case (registry:CodeGeneratorRegistry[Expression], c:SufficientFree) => {
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          val column = registry(c.column).get
          val reserve = registry(c.reserve).get
          val tableau = registry(c.tableau).get
          Java(s"""ConstraintHelper.sufficientFree($column, $src, $destination, $reserve, $tableau)""").expression()
        }
      },

    ).merge(constraintCodeGenerators.generators)
  }

  /**
    * Freecell requires specialized extensions for constraints to work.
    */
  @combinator object FreeCellGenerator {
    def apply: CodeGeneratorRegistry[Expression] = freecellCodeGenerator.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Every solitaire variation exists within a designated Java package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.freecell").name()
    val semanticType: Type = packageName
  }

  /**
    * Each solitaire variation has a name.
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("FreeCell").simpleName()
    val semanticType: Type = variationName
  }

  // FreeCell model derived from the domain model
  @combinator object FreeCellInitModel {

    // note: we could avoid passing in these parameters and just solely
    // visit the domain model. That is an alternative worth considering.

    def apply(): Seq[Statement] = {

      val dg = deckGen ("deck", solitaire.containers.get(SolitaireContainerTypes.Stock))
      val colGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldColumns", "fieldColumnViews", "Column")
      val resGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Reserve), "fieldFreePiles", "fieldFreePileViews", "FreePile")
      val foundGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Foundation), "fieldHomePiles", "fieldHomePileViews", "HomePile")

      dg ++ colGen ++ resGen ++ foundGen
    }

    val semanticType: Type = game(game.model)
  }

  @combinator object FreeCellInitView {
    def apply(): Seq[Statement] = {

      var stmts = Seq.empty[Statement]

      stmts = stmts ++ layout_place_it(solitaire.containers.get(SolitaireContainerTypes.Foundation), Java("fieldHomePileViews").name())
      stmts = stmts ++ layout_place_it(solitaire.containers.get(SolitaireContainerTypes.Reserve), Java("fieldFreePileViews").name())
      stmts = stmts ++ layout_place_it(solitaire.containers.get(SolitaireContainerTypes.Tableau), Java("fieldColumnViews").name())

      stmts
    }

    val semanticType: Type = game(game.view)
  }

  @combinator object FreeCellInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {

      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
      val colsetup = loopControllerGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldColumnViews", "ColumnController")
      val freesetup = loopControllerGen(solitaire.containers.get(SolitaireContainerTypes.Reserve), "fieldFreePileViews", "FreePileController")
      val homesetup = loopControllerGen(solitaire.containers.get(SolitaireContainerTypes.Foundation), "fieldHomePileViews", "HomePileController")

       colsetup ++ freesetup ++ homesetup
    }

    val semanticType: Type = variationName =>: game(game.control)
  }

  // generic deal cards from deck into the tableau
  @combinator object FreeCellInitLayout {
    def apply(): Seq[Statement] = {
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
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
    * Specialized methods to help out in processing constraints. Specifically,
    * these are meant to be generic, things like getTableua, getReserve()
    */
  @combinator object HelperMethodsFreeCell {
    def apply(): Seq[MethodDeclaration] = Seq (generateHelper.fieldAccessHelper("tableau", "fieldColumns"),
      generateHelper.fieldAccessHelper("reserve", "fieldFreePiles"),
      Java(s"""
           |public static boolean sufficientFree (Column column, Stack src, Stack destination, Stack[] reserve, Stack[] tableau) {
           |	int numEmpty = 0;
           |	for (Stack s : tableau) {
           |		if (s.empty() && s != destination) numEmpty++;
           |	}
           |
           | 	// now count columns
           |	for (Stack r : reserve) {
           |		if (r.empty() && r != destination) numEmpty++;
           |	}
           |
           |	return column.count() <= 1 + numEmpty;
           |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration]).head,
    )

    val semanticType: Type = constraints(constraints.methods)
  }

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {

      val reserve = solitaire.containers.get(SolitaireContainerTypes.Reserve).size()
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau).size()
      val numFreePiles: IntegerLiteralExpr = Java(s"$reserve").expression()
      val numColumns: IntegerLiteralExpr = Java(s"$tableau").expression()

      val methods = java.ExtraMethods.render(numFreePiles, numColumns).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

      val solvableMoves = Java(
        s"""
           |public boolean validColumn(Column column) {
           |		return column.alternatingColors() && column.descending();
           |}
           |
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
    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }



   @combinator object MakeHomePile extends ExtendModel("Pile", "HomePile", 'HomePileClass)
   @combinator object MakeFreePile extends ExtendModel("Pile", "FreePile", 'FreePileClass)
   @combinator object MakeHomePileView extends ExtendView("PileView", "HomePileView", "HomePile", 'HomePileViewClass)
   @combinator object MakeFreePileView extends ExtendView("PileView", "FreePileView", "FreePile", 'FreePileViewClass)

   @combinator object FullFoundation {
    def apply(): Seq[Statement] = {
      Java(
        s"""
           |int count = 0;
           |for (HomePile p : fieldHomePiles) {
           |  count += p.count();
           |}
           |if (count == 52) { return true; }
           |""".stripMargin).statements()
    }
    val semanticType: Type = game(game.winCondition)
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

      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
      val reserve = solitaire.containers.get(SolitaireContainerTypes.Reserve)
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      val decks = deckFieldGen(stock)   // note: DeckView not needed for FreeCell

      // Let's assume all from the same type
      val fieldFreePiles = fieldGen(reserve.types().next(),  reserve.size())
      val fieldHomePiles = fieldGen(found.types().next(),  found.size())
      val fieldColumns = fieldGen(tableau.types().next(), tableau.size())

      decks ++ fields ++ fieldFreePiles ++ fieldHomePiles ++ fieldColumns
    }

    val semanticType: Type = game(game.fields)
  }
}
