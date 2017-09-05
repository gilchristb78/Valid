package org.combinators.solitaire.freecell

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared.{GameTemplate, Score52}

// domain
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
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
    def apply(s: Solitaire, rules: Rules, layout:Layout): Solitaire = {
      s.setLayout(layout)
      s.setRules(rules)

      s.setAutoMoves (true);  // we have auto moves.
      s
    }

    val semanticType: Type =
      'Solitaire ('Structure ('FreeCell)) =>:
        'Rules('FreeCell) =>:  
        'Layout ('Valid :&: 'FoundationReserveTableau) =>:  
        'Variation('FreeCell)
  }


  // HACK: non-compositional
  @combinator object FreeCellRules {
    def apply(solitaire:Solitaire): Rules = {
      val rules = new Rules()
      val tableau = solitaire.getTableau
      val reserve = solitaire.getReserve
      val found   = solitaire.getFoundation

      val truth = new ReturnConstraint (new ReturnTrueExpression)
      val falsehood = new ReturnConstraint (new ReturnFalseExpression)
      val isEmpty = new ElementEmpty ("destination")

      // FreePile to FreePile
      val freePileToFreePile = new SingleCardMove("ShuffleFreePile", reserve, reserve, new IfConstraint(isEmpty))
      rules.addDragMove(freePileToFreePile)

      // Column To Free Pile Logic
      val isSingle = new ExpressionConstraint("movingColumn.count()", "==", "1")
      val if1 = new IfConstraint(isEmpty,
                  new IfConstraint(isSingle),
                  falsehood)
      val columnToFreePileMove = new ColumnMove("PlaceColumn", tableau, reserve, if1)
      rules.addDragMove(columnToFreePileMove)

      // Column To Home Pile logic. Just grab first column
      val aCol = tableau.iterator().next()
      val if2 =
        new IfConstraint(isEmpty,
           new IfConstraint(new IsAce(aCol,"movingColumn")),
             new IfConstraint(new NextRank("movingColumn.peek()", "destination.peek()"),
               new IfConstraint(new SameSuit("movingColumn.peek()", "destination.peek()")),
               falsehood))
      val columnToHomePile = new ColumnMove("BuildColumn", tableau, found, if2)
      rules.addDragMove(columnToHomePile)

      // FreePile to HomePile
      val aCard = new Card
      val nonEmpty = new ExpressionConstraint("destination.count()", "!=", "0")
      val if3 =
         new IfConstraint(isEmpty,
           new IfConstraint(new IsAce(aCard, "movingCard")),
             new IfConstraint(new NextRank("movingCard", "destination.peek()"),
               new IfConstraint(new SameSuit("movingCard", "destination.peek()")),
             falsehood))

      val freePileToHomePile = new SingleCardMove("BuildFreePileCard", reserve, found, if3)
      rules.addDragMove(freePileToHomePile)

     // FreePile to Column.
      val if5_inner =
          new IfConstraint(new OppositeColor("movingCard", "destination.peek()"),
            new IfConstraint(new NextRank("destination.peek()", "movingCard")),
              falsehood)

      val if5 = new IfConstraint(isEmpty, truth, if5_inner)
      val freePileToColumnPile = new SingleCardMove("PlaceFreePileCard", reserve, tableau, if5)
      rules.addDragMove(freePileToColumnPile)

     // column to column
     val descend = new Descending("movingColumn")
     val alternating = new AlternatingColors("movingColumn")
      
     val sufficientFreeToEmpty =
         new ExpressionConstraint("((org.combinators.solitaire.freecell.FreeCell)game).numberVacant() - 1", ">=", "movingColumn.count()")

     val sufficientFree =
         new ExpressionConstraint("((org.combinators.solitaire.freecell.FreeCell)game).numberVacant()", ">=", "movingColumn.count() - 1")

      val if4_inner =
        new IfConstraint(new OppositeColor("movingColumn.peek(0)", "destination.peek()"),
          new IfConstraint(new NextRank("destination.peek()", "movingColumn.peek(0)"),
            new IfConstraint(sufficientFree),
            falsehood),
          falsehood)

     val if4 =
        new IfConstraint(descend,
          new IfConstraint(alternating,
            new IfConstraint(isEmpty,
              new IfConstraint(sufficientFreeToEmpty),
              if4_inner),
            falsehood),
          falsehood)
		
      val columnToColumn = new ColumnMove("MoveColumn", tableau, tableau, if4)
      rules.addDragMove(columnToColumn)

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
