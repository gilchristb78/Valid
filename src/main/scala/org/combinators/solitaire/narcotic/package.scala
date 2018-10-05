package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object narcotic {

  val numTableau:Int = 4
  val map:Map[ContainerType,Seq[Element]] = Map(
    Tableau -> Seq.fill[Element](numTableau)(Pile),
    StockContainer -> Seq(Stock(1))
  )

  val narcotic:Solitaire = {

    Solitaire( name="Narcotic",

      structure = Map(),

      layout=stockTableauLayout(2),  // HACK

      deal = Seq(DealStep(ContainerTarget(Tableau))),

      /** from element can infer ks.ViewWidget as well as Base Element. */
      specializedElements = Seq.empty,

      /** All rules here. */
      moves = Seq.empty,

      // fix winning logic
      logic = BoardState(Map(Foundation -> 52))

    )
  }
}
