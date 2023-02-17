package org.combinators.solitaire.fan

import org.combinators.solitaire.domain._
package object trefoil extends variationPoints{

  override val structureMap:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](16)(Column),
    Foundation -> Seq.fill[Element](4)(Pile),
    StockContainer -> Seq(Stock())
  )
  val points2: Seq[(Int, Int)] = Seq ((100,200), (203,200), (306, 200), (409, 200), (512, 200), (615, 200), (100,400), (203,400), (306, 400), (409, 400), (512, 400), (615, 400), (100,600), (203,600), (306, 600), (409, 600))
  override val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> calculatedPlacement(points2, height = card_height*2),
    Foundation -> horizontalPlacement(200, 10, 4, card_height),
  )

  def getDeal2: Seq[Step] = {
    var deal: Seq[Step] = Seq(FilterStep(IsAce(DealComponents)))
    for (i <- 0 to 3 ) {
      deal = deal :+ DealStep(ElementTarget(Foundation, i))
    }
    for (i <- 0 to 15){
      deal = deal :+ DealStep(ElementTarget(Tableau, i), Payload(faceUp=true, 3))
    }
    deal
  }
  val trefoil: Solitaire = {
    Solitaire(name = "Trefoil",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal2,
      specializedElements = Seq.empty,  //should have a redeal
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Tableau-> 0, Foundation->52)),
      solvable = true,
      customizedSetup = Seq(TableauToEmptyFoundation, TableauToNextFoundation, TableauToEmptyTableau, TableauToNextTableau)
    )
  }
}