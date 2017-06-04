package org.combinators.solitaire.freecell

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared.{GameTemplate, Score52}

// domain
import domain._
import domain.constraints._
import domain.moves._
import domain.freeCell.{FreePile, HomePile}

trait Game extends GameTemplate with Score52 {

  //lazy val alpha = Variable("alpha")

  // extend existing kinding
  lazy val newKinding: Kinding =
    Kinding(tableauType)
      .addOption('FourColumnTableau)
      .addOption('EightColumnTableau)

    // Free cell is an example solitaire game that uses Foundation, Reserve, and Tableau.
  @combinator object FreeCellStructure {
    def apply(s: Solitaire, f: Foundation, r: Reserve, t: Tableau, st: Stock): Solitaire = {
      s.setFoundation(f)
      s.setReserve(r)
      s.setTableau(t)
      s.setStock(st)

      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) :&: 'Solitaire ('Foundation ('None)) :&: 'Solitaire ('Reserve ('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None)) =>:
        'Foundation ('Valid :&: 'HomePile) =>:
        'Reserve ('Valid :&: 'FreePile) =>:
        'Tableau ('Valid :&: 'Eight :&: 'Column) =>:
        'Stock ('Valid :&: 'OneDeck) =>:
        'Solitaire ('Structure ('FreeCell))
  }



  // Free cell is an example solitaire game that uses Foundation, Reserve, and Tableau.
  @combinator object FreeCellConstruction {
    def apply(s: Solitaire, lay: Layout, rules: Rules): Solitaire = {
      s.setLayout(lay)
      s.setRules(rules)

      s
    }

    val semanticType: Type =
      'Solitaire ('Structure ('FreeCell)) =>:
        'Layout ('Valid :&: 'FoundationReserveTableau) =>:
        'Rules('FreeCell) =>: 
        'FreeCellVariation
  }

  // HACK: non-compositional
  @combinator object FreeCellRules {
    def apply(solitaire:Solitaire): Rules = {
      val rules = new Rules()
      val tableau = solitaire.getTableau
      val reserve = solitaire.getReserve
      val found = solitaire.getFoundation

      val c2a = AndConstraint.builder(new AlternatingColors("movingColumn"))
                .add(new Descending("movingColumn"))
		.add(new BaseCardOneHigherOppositeColor())
		.add(new SufficientFree())
		
      rules.addMove(new ColumnMove(tableau, tableau, c2a))
      rules.addMove(new SingleCardMove(reserve, tableau, c2a))

      // can move a column from tableau to reserve, if empty
      // can move a single card from reserve to reserve, if empty
      val c3 = new ElementEmpty("destination")
      rules.addMove(new ColumnMove(tableau, reserve, c3))
      rules.addMove(new SingleCardMove(reserve, reserve, c3))

      val aCol = new Column
      val aCard = new Card

      val c4b_column = AndConstraint.builder(new IsAce(aCol, "movingColumn"))
		   .add(new ElementEmpty("destination"))      

      val c4b_card = AndConstraint.builder(new IsAce(aCard, "movingCard"))
                   .add(new ElementEmpty("destination"))

      val c4d = AndConstraint.builder(new ExpressionConstraint("destination.count()", "!=", "0"))
		   .add(new NextRank("destination.peek()", "movingColumn.peek()"))
		   .add(new SameSuit("destination.peek()", "movingColumn.peek()"))

      val c4_col = new OrConstraint(c4b_column, c4d)
      val c4_card = new OrConstraint(c4b_card, c4d)
 
      rules.addMove(new ColumnMove(tableau, found, c4_col))
      rules.addMove(new SingleCardMove(reserve, found, c4_card))

      rules
    }

    val semanticType:Type = 'Solitaire('Structure('FreeCell)) =>: 'Rules('FreeCell)
  }
 
  // 4-HomePile Foundation
  @combinator object FourHomePileFoundation {
    def apply(): Foundation = {
      val f = new Foundation()

      f.add(new HomePile()) // put into for-loop soon.
      f.add(new HomePile())
      f.add(new HomePile())
      f.add(new HomePile())

      println("setting four HomePile Foundation")

      f
    }

    val semanticType: Type = 'Foundation ('Valid :&: 'Four :&: 'HomePile)
  }

  // in FreeCell we need a valid foundation.
  @combinator object AddFourPileFoundation {
    def apply(s: Solitaire, f: Foundation): Solitaire = {
      s.setFoundation(f)
      println("setting four-pile foundation.")
      s
    }

    val semanticType: Type =
      'Solitaire ('Foundation ('None)) =>: 'Foundation ('Valid :&: 'HomePile) =>:
        'Solitaire ('Foundation ('Valid :&: 'HomePile))
  }

  // 4-HomePile Foundation
  @combinator object FourHomePileReserve {
    def apply(): Reserve = {
      val r = new Reserve()

      r.add(new FreePile()) // put into for-loop soon.
      r.add(new FreePile())
      r.add(new FreePile())
      r.add(new FreePile())

      println("setting four FreePile Reserve")

      r
    }

    val semanticType: Type = 'Reserve ('Valid :&: 'Four :&: 'FreePile)
  }

  // in FreeCell we need a stock composed of a single deck.
  @combinator object SingleDeckStock {
    def apply(): Stock = new Stock()

    val semanticType: Type = 'Stock ('Valid :&: 'OneDeck)
  }


  // in FreeCell we need a valid reserve
  @combinator object AddFourPileReserve {
    def apply(s: Solitaire, r: Reserve): Solitaire = {
      s.setReserve(r)
      println("setting four-pile reserve.")
      s
    }

    val semanticType: Type =
      'Solitaire ('Reserve ('None)) =>: 'Reserve ('Valid :&: 'FreePile) =>:
        'Solitaire ('Reserve ('Valid :&: 'FreePile))
  }

  // in FreeCell we need a valid tableau. Not sure why we have to
  // restrict that here to be 8; could still be searched
  @combinator object AddEightColumnTableau {
    def apply(s: Solitaire, tab: Tableau): Solitaire = {
      s.setTableau(tab)
      println("setting eight-column tableau")
      s
    }

    val semanticType: Type =
      'Solitaire ('Tableau ('None)) =>: 'Tableau ('Valid :&: 'Column) =>:
        'Solitaire ('Tableau ('Valid :&: 'Column))
  }

}
