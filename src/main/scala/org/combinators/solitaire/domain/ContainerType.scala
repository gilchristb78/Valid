package org.combinators.solitaire.domain

/**
  * unsealed trait since variation-specific
  * extensions are possible.
  *
  * Important for ContainerType to extend MoveInformation, since there are constraints placed onto
  * moves as such
  */
trait ContainerType extends MoveInformation {
  // case objects may have $ in their name
  val name:String = getClass.getSimpleName.replace("$","").toLowerCase()

  /** By default each container refers to a potential collection. */
  val isSingleCard:Boolean = false
}

/** Generic container types. Variations can add new ones as needed. */
case object Tableau extends ContainerType
case object Foundation extends ContainerType
case object StockContainer extends ContainerType   // Causing problems. Should be Deck? But then not consistent
case object Reserve extends ContainerType
case object Waste extends ContainerType

