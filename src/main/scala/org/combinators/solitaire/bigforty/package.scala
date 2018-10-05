package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object bigforty {

  val bigforty:Solitaire = {

    Solitaire( name="Bigforty",

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
