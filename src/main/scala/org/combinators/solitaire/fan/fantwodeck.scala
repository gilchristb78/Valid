package org.combinators.solitaire.fan

import org.combinators.solitaire.domain._

package object fantwodeck extends variationPoints{
   val points2: Seq[(Int, Int)] = Seq ((100,200), (203,200), (306, 200), (409, 200), (512, 200), (615, 200), (718,200), (100,400), (203,400), (306, 400), (409, 400), (512, 400), (615, 400), (718,400), (100,600), (203,600), (306, 600), (409, 600), (512, 600), (615, 600), (718, 600), (100,800), (203,800), (306, 800), (409, 800), (512, 800))
   override val layoutMap:Map[ContainerType, Seq[Widget]] = Map (
    Tableau -> calculatedPlacement(points2, height = card_height*2),
    Foundation -> horizontalPlacement(100, 10, 8, card_height),
  )
  override val structureMap: Map[ContainerType, Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](26)(Column),
    Foundation -> Seq.fill[Element](8)(Pile),
    StockContainer -> Seq(Stock(2))
  )
  override def getDeal: Seq[DealStep] = {
    var deal:Seq[DealStep] = Seq()
    var colNum = 0
    while (colNum < 26)
    {
      deal = deal :+ DealStep(ElementTarget(Tableau, colNum), Payload(numCards =  4))
      colNum+=1
    }
    deal
  }

  val fantwodeck: Solitaire = {
    Solitaire(name = "FanTwoDeck",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove),
      logic = BoardState(Map(Tableau -> 0, Foundation -> 104)),
      solvable = true,
      customizedSetup = Seq(TableauToEmptyFoundation, TableauToNextFoundation, TableauToEmptyTableau, TableauToNextTableau)
    )
  }
}