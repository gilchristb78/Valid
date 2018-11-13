package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object TEMPLATE {
  val map:Map[ContainerType,Seq[Element]] = Map(
  )

  val tEMPLATE:Solitaire = {
    Solitaire( name="TEMPLATE",
      structure = map,
      layout= Layout,
      deal = Seq.empty,
      specializedElements = Seq.empty,
      moves = Seq.empty,
      logic = BoardState(Map())
    )
  }
}