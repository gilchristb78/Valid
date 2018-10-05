package org.combinators.solitaire.domain

// dealing is a sequence of steps
case class Payload(faceUp:Boolean = true, numCards:Int = 1)

trait Step { }
trait Target {}


trait MapType

case object MapByRank extends MapType
case object MapBySuit extends MapType

case class DealStep(target:Target, payload:Payload = Payload()) extends Step
case class FilterStep(c:Constraint, limit:Int = -1) extends Step
case class MapStep(target:ContainerType, payload:Payload, mapping:MapType) extends Step

/** A designated target based on the container. */
case class ContainerTarget(typ:ContainerType) extends Target

/** When target is a designated element within Container (by idx). */
case class ElementTarget(containerType:ContainerType, positionInContainer: Int) extends Target
