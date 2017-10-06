package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.stalactites._
import domain._
import domain.ui.Layout
import org.combinators.generic
import org.combinators.solitaire.shared.{GameTemplate, Score52}

trait Game extends GameTemplate with Score52 with generic.JavaIdioms {

  /**
    * Stalactites's domain structure has:
    * - Tableau in 8columns of 6 cards each,
    * - Foundation of four piles
    * - Reserve in 2 piles
    */
  @combinator object StalactitesStructure {
    def apply(sol: Solitaire, t: Tableau, r: Reserve, f:Foundation): Solitaire = {
      sol.setTableau(t)
      sol.setReserve(r)
      sol.setFoundation(f)
      sol
    }

    // When given an empty Solitaire type, give back the semantic structure of Stalactites.
    val semanticType: Type =
      ('Solitaire ('Reserve ('None) :&: 'Tableau ('None) :&: 'Foundation('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None))) =>:
        'Tableau ('Valid :&: 'Eight :&: 'Column) =>:
        'Reserve ('Valid :&: 'Two :&: 'ReservePile) =>:
        'Foundation ('Valid :&: 'Four :&: 'Pile) =>:
        'Solitaire ('Structure ('Stalactites))
  }

  /**
    * Given a Stalactites Structure, its Rules, and its Layout,
    * return the Stalactites variation.
    */
  @combinator object Construction {
    def apply(sol: Solitaire, rules: Rules, layout: Layout): Solitaire = {
      sol.setLayout(layout)
      sol.setRules(rules)
      sol
    }

    val semanticType: Type =
      'Solitaire ('Structure ('Stalactites)) =>:
        'Rules('Stalactites) =>:
        'Layout('Valid :&: 'StalactitesLayout) =>:
        'Variation('Stalactites)
  }

  /** Generate Reserve, Tableau, Foundation */

  /** Manually generate the Aces Foundation, because it's split between Aces and Kings.
    * The default NPileFoundation generates new Pile(), but we need AcesUpPile(), a subclass.
    */
  @combinator object StalactitesReserve {
    def apply(): Reserve = {
      val t = new Reserve()

      for (_ <- 1 to 2)
        t.add(new ReservePile())

      t
    }
    val semanticType: Type = 'Reserve ('Valid :&: 'Two :&: 'ReservePile)
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
      'SolitaireVariation :&: 'increment)


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

  @combinator object StalactitesRules {
    def apply(solitaire: Solitaire): Rules = {
      val rules = new Rules()

      // Get Domain objects from Solitaire Object.
//      val tableau = solitaire.getTableau
//      val reserve = solitaire.getReserve
//      val acesFoundation = solitaire.getFoundation
//      val kingsFoundation = solitaire.getContainer("KingsDownFoundation")

//
//
//      /* Contraint saying if the moving card has the same suit as the top-facing destination card. */
//      // Note that these are real fields.
//      val sameSuit = new SameSuit("movingCard", "destination.peek()")
//
//      /* A Card can move to the Aces Foundation if the moving card is
//       * one rank higher and has the same suit.
//       */
//      val moveToAcesCondition = new IfConstraint(
//        new AndConstraint(
//          new NextRank("movingCard", "destination.peek()"),
//          sameSuit))
//
//      /* A Card can move to the Kings Foundation if the moving card is
//       * one rank lower and has the same suit.
//       */
//      val moveToKingsCondition = new IfConstraint(
//        new AndConstraint(
//          new NextRank("destination.peek()", "movingCard"),
//          sameSuit))
//
//      /* Add Rules */
//
//      // Note that the string argument becomes a real classname.
//      rules.addDragMove(
//        new SingleCardMove("TableauToFoundation",
//          tableau, acesFoundation, moveToAcesCondition
//        )
//      )
//
//      rules.addDragMove(
//        new SingleCardMove("ReserveToFoundation",
//          reserve, acesFoundation, moveToAcesCondition
//        )
//      )
//
//      rules.addDragMove(
//        new SingleCardMove("TableauToKingsFoundation",
//          tableau, kingsFoundation, moveToKingsCondition
//        )
//      )
//
//      rules.addDragMove(
//        new SingleCardMove("ReserveToKingsFoundation",
//          reserve, kingsFoundation, moveToKingsCondition
//        )
//      )
//
//      rules.addDragMove(
//        new SingleCardMove("ReserveToTableau",
//          reserve, tableau, new IfConstraint(new ElementEmpty("destination")))
//      )
//
//      rules.addDragMove(
//        new SingleCardMove("TableauToTableau", tableau, tableau, new IfConstraint(new ReturnFalseExpression))
//      )

      rules
    }
    val semanticType:Type = 'Solitaire('Structure('Stalactites)) =>: 'Rules('Stalactites)
  }

  /**
    * Defines Stalactites's placement of the Reserve, Foundations, and Tableau.
    */
  @combinator object StalactitesLayout {
    def apply(): Layout = {
      val lay = new Layout()

      val card_width = 73
      val card_height = 97

      // signature is (name, x, y, width, height)
      lay.add(Layout.Reserve,        15,   20,  2 * card_width + 40, card_height)
      lay.add(Layout.Foundation,     15,  200,  4 * card_width + 80, card_height)
      lay.add(Layout.Tableau,        15,  260,  8 * card_width + 80, 13 * card_height)
      lay
    }
    val semanticType: Type = 'Layout ('Valid :&: 'StalactitesLayout)
  }
}
