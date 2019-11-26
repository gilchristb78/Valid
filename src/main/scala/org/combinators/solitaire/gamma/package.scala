package org.combinators.solitaire

import org.combinators.solitaire.domain._

package object gamma {
  val structureMap: Map[ContainerType, Seq[Element]] = Map(
  )

  val layoutMap: Map[ContainerType, Seq[Widget]] = Map(
  )

  val gamma: Solitaire = {
    Solitaire(name = "gamma",
      structure = structureMap,
      layout = Layout(layoutMap),
      deal = Seq.empty,
      specializedElements = Seq.empty,
      moves = Seq.empty,
      logic = BoardState(Map()),
      testSetup = Seq()
    )
  }
}