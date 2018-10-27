package org.combinators.solitaire.domain

/** Base interface for all possible constraints used within the system. */
trait MoveInformation {

  /**
    * Elements described in a move might be a single card or a collection of cards.
    *
    * Return true if given element is a single card; false if represents potentially multiple cards.
    */
  val isSingleCard:Boolean
}

trait MoveComponents extends MoveInformation {
  val isSingleCard:Boolean = false
}

case object Source extends MoveComponents
case object Destination extends MoveComponents

/** When moving more than one card. */
case object MovingCards extends MoveComponents

/** When moving just a single card. */
case object MovingCard extends MoveComponents {
  override val isSingleCard:Boolean = true
}

case object DealComponents extends MoveComponents {
  override val isSingleCard:Boolean = true
}

/** The bottom card of the given moveInfo concept. */
case class BottomCardOf(moveInfo:MoveInformation) extends MoveInformation {
  override val isSingleCard:Boolean = true
}

/** The top card of the given moveInfo concept. */
case class TopCardOf(moveInfo:MoveInformation) extends MoveInformation {
  override val isSingleCard:Boolean = true
}