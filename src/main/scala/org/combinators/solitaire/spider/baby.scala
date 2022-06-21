package org.combinators.solitaire

import org.combinators.solitaire.domain._
import org.combinators.solitaire.spider.closedVariationPoints
import org.combinators.templating.twirl.Java


package object baby extends closedVariationPoints {

  override def numTableau: Int = 8
  override def numFoundation: Int = 4
  override def numStock: Int = 1

  override def getDeal: Seq[DealStep] = {
    var colNum: Int = 1
    var dealSeq: Seq[DealStep] = Seq() // doesn't like me declaring it without initializing
    // Klondike deal - the ith pile gets i face down cards
    for (colNum <- 1 to 7) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = false, numCards = colNum))
    }
    //each pile gets a face up card
    colNum = 0
    for (colNum <- 0 to 7) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload())
    }

    dealSeq
  }

  override def buildOnTableau(cards: MovingCards.type): Constraint = {
    val topDestination = TopCardOf(Destination)
    val bottomMoving = BottomCardOf(cards)
    val isEmpty = IsEmpty(Destination)
    val descend = Descending(cards)

    AndConstraint( descend, OrConstraint(isEmpty, NextRank(topDestination, bottomMoving, wrapAround=true)) )
  }

  case object PrepareTableauToFoundation extends Setup {

    val sourceElement: ElementInContainer = ElementInContainer(Tableau, 0)
    val targetElement: Some[ElementInContainer] = Some(ElementInContainer(Foundation, 2))

    // clear Foundation, and place [2C, AC] on 0th tableau
    val setup:Seq[SetupStep] = Seq(
      RemoveStep(sourceElement),
      InitializeStep(targetElement.get, CardCreate(Hearts, Ace)),
      InitializeStep(targetElement.get, CardCreate(Hearts, Two)),
    )

    // Note: The premise behind falsifiedTest() is flawed. Specifically, given a condition
    // that is OR(c1, c2) and if you attempt to falsify with OR(not c1, c2) to demonstrate
    // an error, it could still succeed, because of c2. So we are only going to work on
    // positive test cases, to validate that a move works.
  }

  val baby:Solitaire = {
    Solitaire(name = "Baby",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove, flipMove),
      logic = BoardState(Map(Foundation -> 52)),
      customizedSetup = Seq(PrepareTableauToFoundation)
    )
  }
}