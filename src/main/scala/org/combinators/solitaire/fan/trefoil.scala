package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.fan.variationPoints
import org.combinators.solitaire.labellelucie.{buildOnTableau, tableauToFoundationMove, tableauToTableauMove2}
package object trefoil extends variationPoints{

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](16)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock(1))
  )
  val points2: Seq[(Int, Int)] = Seq ((100,200), (203,200), (306, 200), (409, 200), (512, 200), (615, 200), (100,400), (203,400), (306, 400), (409, 400), (512, 400), (615, 400), (100,600), (203,600), (306, 600), (409, 600))
  override val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> calculatedPlacement(points2, height = card_height*2),
    Foundation -> horizontalPlacement(200, 10, 4, card_height),
  )
  val tt_move2:Constraint = AndConstraint(NotConstraint(IsEmpty(Destination)), buildOnTableau(MovingCard))

  val tableauToTableauMove2:Move = SingleCardMove("MoveCard", Drag,
    source=(Tableau,Truth), target=Some((Tableau, tt_move2)))

  def getDeal2: Seq[Step] = {
    var deal: Seq[Step] = Seq(FilterStep(IsAce(DealComponents)))
    for (i <- 0 to 3 ) {
      deal = deal :+ DealStep(ElementTarget(Foundation, i))
    }
    for (i <- 0 to 15){
      deal = deal :+ DealStep(ElementTarget(Tableau, i), Payload(true, 3))
    }
    deal
  }
  val trefoil: Solitaire = {
    Solitaire(name = "trefoil",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal2,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove2, tableauToFoundationMove),
      logic = BoardState(Map(Tableau-> 0, Foundation->52)),
      solvable = true,
      testSetup = Seq(),
    )
  }
}