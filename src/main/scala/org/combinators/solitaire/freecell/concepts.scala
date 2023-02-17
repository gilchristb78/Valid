package org.combinators.solitaire.freecell

import org.combinators.solitaire.domain.{Constraint, Element, MoveInformation}

case class IsSufficientFree(movingCards: MoveInformation, from: MoveInformation, to: MoveInformation) extends Constraint
case object FreeCellPile extends Element(true)
