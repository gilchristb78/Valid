package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.generic.JavaIdioms

// domain
import domain._

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with JavaIdioms with Score52 {

  /**
    * Every solitaire variation exists within a designated Java package.
    */
  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.stalactites").name()
    val semanticType: Type = 'RootPackage
  }

  /**
    * Each solitaire variation has a name.
    */
  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Stalactites").simpleName()
    val semanticType: Type = 'NameOfTheGame
  }

  @combinator object MakeReservePile        extends ExtendModel("Pile",    "ReservePile",    'ReservePileClass)
  @combinator object MakeReservePileView    extends ExtendView("PileView", "ReservePileView", "ReservePile", 'ReservePileViewClass)

  @combinator object MakeBasePile        extends ExtendModel("Pile",    "BasePile",    'BasePileClass)
  @combinator object MakeBasePileView    extends ExtendView("PileView", "BasePileView", "BasePile", 'BasePileViewClass)


  // Stalactites model derived from the domain model
  @combinator object InitModel {

    // note: we could avoid passing in these parameters and just solely
    // visit the domain model. That is an alternative worth considering.

    def apply(): Seq[Statement] = {

      val dg = deckGen ("deck")
      val foundGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Foundation), "fieldPiles", "fieldPileViews", "Pile")
      val colGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Tableau), "fieldColumns", "fieldColumnViews", "Column")
      val resGen = loopConstructGen(solitaire.containers.get(SolitaireContainerTypes.Reserve), "fieldReservePiles", "fieldReservePileViews", "ReservePile")
      val base = loopConstructGen(solitaire.containers.get(StalactitesContainerTypes.Base), "fieldBasePiles", "fieldBasePileViews", "BasePile")

      dg ++ colGen ++ resGen ++ foundGen ++ base
    }

    val semanticType: Type = 'Init ('Model)
  }

  @combinator object CellInitView {
    def apply(): Seq[Statement] = {

      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val reserve = solitaire.containers.get(SolitaireContainerTypes.Reserve)
      val base = solitaire.containers.get(StalactitesContainerTypes.Base)
      var stmts = Seq.empty[Statement]

      stmts = stmts ++ layout_place_it(found, Java("fieldPileViews").name())
      stmts = stmts ++ layout_place_it(reserve, Java("fieldReservePileViews").name())
      stmts = stmts ++ layout_place_it(tableau, Java("fieldColumnViews").name())
      stmts = stmts ++ layout_place_it(base, Java("fieldBasePileViews").name())
      stmts
    }

    val semanticType: Type = 'Init ('View)
  }

  @combinator object CellInitControl {
    def apply(NameOfGame: SimpleName): Seq[Statement] = {

      // this could be controlled from the UI model. That is, it would
      // map GUI elements into fields in the classes.
//      val colsetup = loopControllerGen(solitaire.getTableau, "fieldColumnViews", "ColumnController")
//      val reservesetup = loopControllerGen(solitaire.getReserve, "fieldReservePileViews", "ReservePileController")
//      val homesetup = loopControllerGen(solitaire.getFoundation, "fieldPileViews", "PileController")
//
//       colsetup ++ reservesetup ++ homesetup
      Seq.empty
    }

    val semanticType: Type = 'NameOfTheGame =>: 'Init ('Control)
  }

  // generic deal cards from deck into the tableau
  @combinator object CellInitLayout {
    def apply(): Seq[Statement] = {
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      // standard logic to deal to all tableau cards
      val numColumns = tableau.size
      Java(
        s"""for (int i = 0; i < 4; i++) {
           | 	fieldBasePiles[i].add(deck.get());
           |}
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
    def apply(): Seq[MethodDeclaration] = Seq.empty
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

      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
      val reserve = solitaire.containers.get(SolitaireContainerTypes.Reserve)
      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val base = solitaire.containers.get(StalactitesContainerTypes.Base)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      val decks = deckFieldGen(stock)   // note: DeckView not needed for

      val fieldReservePiles = fieldGen("ReservePile", reserve.size)
      val fieldPiles = fieldGen("Pile", found.size)
      val fieldColumns = fieldGen("Column",  tableau.size)
      val fieldCards = fieldGen("BasePile", base.size)

      decks ++ fields ++ fieldReservePiles ++ fieldPiles ++ fieldColumns ++ fieldCards
    }

    val semanticType: Type = 'ExtraFields
  }

  // desire to consolidate into one place everything having to do with increment capability
  // Defined in one place and then. Cross-cutting combinator.

  // Finally applies the weaving of the Increment concept by takings its constituent fields and method declarations
  // and injecting them into the compilation unit.
  @combinator object IncrementCombinator
    extends GetterSetterMethods(
      Java("increment").simpleName(),
      Java("int").tpe(),
      'SolitaireVariation,
      'increment)


  /**
    * Fundamental to Undo of moves to foundation is the ability to keep track of the last orientation.
    */
  @combinator object RecordNewOrientation {
    def apply(root: Name, name: SimpleName): Seq[Statement] = {
      Java(
        s"""
           |$root.$name stalactites = ($root.$name)game;
           |lastOrientation = stalactites.getIncrement();
           |stalactites.setIncrement(orientation);
           """.stripMargin).statements()
    }

    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'RecordOrientation
  }

  /**
    * Go back to earlier orientation
    */
  @combinator object RevertToEarlierOrientation {
    def apply(root: Name, name: SimpleName): Seq[Statement] = {
      Java(
        s"""
           |$root.$name stalactites = ($root.$name)game;
           |stalactites.setIncrement(lastOrientation);
           """.stripMargin).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'UndoOrientation
  }


  /**
    * Need for helper
    */
  @combinator object HelperMethodsFreeCell {
    def apply(): Seq[MethodDeclaration] = Seq.empty

    val semanticType: Type = 'HelperMethods
  }

}
