package org.combinators.solitaire.domain

trait Constraint

case class AlternatingColors(on: MoveInformation) extends Constraint
case class Descending (on: MoveInformation) extends Constraint
case class HigherRank(higher: MoveInformation, lower:MoveInformation) extends Constraint
case class IsAce(on: MoveInformation) extends Constraint
case class IsEmpty(on: MoveInformation) extends Constraint
case class IsFaceUp(on: MoveInformation) extends Constraint
case class IsKing(on: MoveInformation) extends Constraint
case class IsRank(on: MoveInformation, rank: Rank) extends Constraint
case class IsSingle(on: MoveInformation) extends Constraint
case class IsSuit(on: MoveInformation, suit:Suit) extends Constraint

// choose to wrap around (or not)
case class NextRank(higher: MoveInformation, lower:MoveInformation, wrapAround:Boolean = false) extends Constraint
case class OppositeColor(on: MoveInformation, other:MoveInformation) extends Constraint
case class SameColor(on: MoveInformation, other:MoveInformation) extends Constraint
case class SameRank(on: MoveInformation, other:MoveInformation) extends Constraint
case class SameSuit(on: MoveInformation, other:MoveInformation) extends Constraint

case object Truth extends Constraint
case object Falsehood extends Constraint

case class IfConstraint(guard: Constraint, trueBranch:Constraint = Truth, falseBranch:Constraint = Falsehood) extends Constraint
case class NotConstraint(inner: Constraint) extends Constraint
case class OrConstraint(args: Constraint*) extends Constraint
case class AndConstraint(args: Constraint*) extends Constraint
