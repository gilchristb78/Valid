package org.combinators.solitaire

import org.combinators.solitaire.domain._


package object archway {

  case object AcesUpPile extends Element (true)
  case object KingsDownPile extends Element (true)

  case object KingsDownFoundation extends ContainerType

  val numTableau:Int = 4
  val numFoundation:Int = 4
  val numReserve:Int = 13
  val map:Map[ContainerType,Seq[Element]] = Map(
    StockContainer -> Seq.fill[Element](1)(Stock(2)),
    Foundation -> Seq.fill[Element](numFoundation)(AcesUpPile),
    Tableau -> Seq.fill[Element](numTableau)(Column),
    Reserve -> Seq.fill[Element](numReserve)(Pile),
    KingsDownFoundation -> Seq.fill[Element](numFoundation)(KingsDownPile)
  )

  def cardByRankAndSuit(r:Rank, s:Suit):FilterStep = {
    FilterStep(AndConstraint(IsRank(DealComponents, r), IsSuit(DealComponents, s)), 1)
  }

  def archwayLayout():Layout = {
    val scale = 27

    def expand(pts:Seq[(Int,Int)]) : Seq[(Int, Int)] = { pts.map(p => (p._1*scale, p._2*scale))}

    val aces = calculatedPlacement(expand(Seq((2, 23), (5,23), (2, 27), (5, 27))))
    val kings = calculatedPlacement(expand(Seq((23,23), (26,23), (23, 27), (26, 27))))
    val reserve = calculatedPlacement(expand(Seq((2,19), (2,15), (2, 11), (2, 7), (4, 3), (10, 1), (14, 1), (18, 1), (24, 3), (26, 7), (26, 11), (26, 15), (26, 19))))
    val tableau = horizontalPlacement(10*scale, 10*scale, numTableau, 8*card_height)

    Layout(Map(
      Foundation -> aces,
      KingsDownFoundation -> kings,
      Reserve -> reserve,
      Tableau -> tableau
    ))
  }

  val sameSuit = SameSuit(MovingCard, TopCardOf(Destination))

  val moveToAcesCondition = AndConstraint(NextRank(MovingCard, TopCardOf(Destination)), sameSuit)
  val moveToKingsCondition = AndConstraint( NextRank(TopCardOf(Destination), MovingCard), sameSuit)

  val tableauFoundation:Move = SingleCardMove("TableauToFoundation", Drag, source=(Tableau,Truth), target=Some((Foundation, moveToAcesCondition)))
  val tableauKingsFoundation:Move = SingleCardMove("TableauToKingsFoundation", Drag, source=(Tableau,Truth), target=Some((KingsDownFoundation, moveToKingsCondition)))

  val reserveFoundation:Move = SingleCardMove("ReserveToFoundation", Drag, source=(Reserve,Truth), target=Some((Foundation, moveToAcesCondition)))
  val reserveKingsFoundation:Move = SingleCardMove("ReserveToKingsFoundation", Drag, source=(Reserve,Truth), target=Some((KingsDownFoundation, moveToKingsCondition)))

  val reserveTableau:Move = SingleCardMove("ReserveToTableau", Drag, source=(Reserve,Truth), target=Some((Tableau, IsEmpty(Destination))))
  val tableauTableau:Move = SingleCardMove("TableauToTableau", Drag, source=(Tableau,Truth), target=Some((Tableau, Falsehood)))

  val archway:Solitaire = {
    Solitaire( name="Archway",
      solvable = true,

      structure = map,

      layout = archwayLayout(),

      deal = suits.all.reverse.map(s => cardByRankAndSuit(Ace, s)) ++
        Seq(DealStep(ContainerTarget(Foundation))) ++
        suits.all.reverse.map(s => cardByRankAndSuit(King, s)) ++
        Seq(DealStep(ContainerTarget(KingsDownFoundation)), DealStep(ContainerTarget(Tableau), Payload(numCards = 12))) ++
        Seq(MapStep(Reserve, Payload(numCards = 104-8-48), MapByRank)),

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq(AcesUpPile, KingsDownPile),

      /** All rules here: Do not (by mistake) enter same one twice; leads to explosion in states */
      moves = Seq(tableauFoundation, tableauKingsFoundation, reserveFoundation, reserveKingsFoundation, reserveTableau, tableauTableau),

      // fix winning logic
      logic = BoardState(Map(KingsDownFoundation -> 52, Foundation -> 52))
    )
  }
}
