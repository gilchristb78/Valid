package org.combinators.solitaire.klondike

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared.{GameTemplate, Score52}

// domain
import domain._
import domain.ui._

trait Game extends GameTemplate with Score52 {


  /**
    * Given an empty Solitaire domain object, construct the structure of Klondike using a WastePile,
    * Stock, Foundation and Tableau.
    */
  @combinator object CellStructure {
    def apply(s: Solitaire, t: Tableau, st: Stock, f:Foundation, w:Waste): Solitaire = {
      s.setTableau(t)
      s.setStock(st)
      s.setFoundation(f)
      s.setWaste(w)

      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None)) =>:
        'Tableau ('Valid :&: 'Seven :&: 'Column) =>:
        'Stock ('Valid :&: 'One :&: 'Deck) =>:
        'Foundation ('Valid :&: 'Four :&: 'Pile) =>:
        'Waste ('Valid :&: 'One :&: 'Pile) =>:
        'Solitaire ('Structure ('Klondike))
  }

  @combinator object SevenColumnTableau extends NColumnTableau(7, 'Seven)

  // 4-HomePile Foundation
  @combinator object WastePileStructure {
    def apply(): Waste = {
      val r = new Waste()

      r.add(new WastePile())

      println("setting single pile WastePile")

      r
    }

    val semanticType: Type = 'Waste ('Valid :&: 'One :&: 'Pile)
  }


  // 4-HomePile Foundation
  @combinator object FourHomePile {
    def apply(): Foundation = {
      val r = new Foundation()

      r.add(new Pile()) // put into for-loop soon.
      r.add(new Pile())
      r.add(new Pile())
      r.add(new Pile())

      println("setting four Pile Foundation")

      r
    }

    val semanticType: Type = 'Foundation ('Valid :&: 'Four :&: 'Pile)
  }

  @combinator object Construction {
    def apply(s: Solitaire, rules: Rules, layout:Layout): Solitaire = {
      s.setLayout(layout)
      s.setRules(rules)

      s
    }

    val semanticType: Type =
      'Solitaire ('Structure ('Klondike)) =>:
        'Rules('Klondike) =>:
        'Layout ('Valid :&: 'Klondike) =>:
        'Variation('Klondike)
  }


  /**
    * Common layout for solitaire games with just Stcok on left and tableau on right.
    */
  @combinator object KlondikeLayout {
    def apply(): Layout = {
      val lay = new Layout()
      lay.add(Layout.Stock, 15, 20, 73, 97)
      lay.add(Layout.WastePile, 95, 20, 73, 97)
      lay.add(Layout.Foundation, 390, 20, 680, 97)
      lay.add(Layout.Tableau, 40, 200, 1360, 13*97)

      lay
    }

    val semanticType: Type = 'Layout ('Valid :&: 'Klondike)
  }

  // HACK: non-compositional. Also embeds UI indications here as well,
  // but that is just a convenience. Can be moved elsewhere.
  @combinator object Rules {
    def apply(solitaire:Solitaire): Rules = {
      val rules = new Rules()

      rules
    }

    val semanticType:Type = 'Solitaire('Structure('Klondike)) =>: 'Rules('Klondike)
  }

  @combinator object AddFourColumnTableau {
    def apply(s: Solitaire, tab: Tableau): Solitaire = {
      s.setTableau(tab)
      println("setting four-column tableau")
      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) =>: 'Tableau ('Valid :&: 'Column) =>:
        'Solitaire ('Tableau ('Valid :&: 'Column))
  }

}
