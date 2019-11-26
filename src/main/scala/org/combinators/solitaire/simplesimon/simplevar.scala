package org.combinators.solitaire

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import org.combinators.solitaire.domain._
import org.combinators.solitaire.simplesimon.variationPoints
import org.combinators.templating.twirl.Java


package object simplevar extends variationPoints {

  override def numTableau(): Int = {
    8
  }

  override def numStock(): Int = {
    1
  }

  override def getDeal: Seq[DealStep] = {
    var colNum:Int = 0
    var dealSeq:Seq[DealStep] = Seq()// doesn't like me declaring it without initializing
    for (colNum <- 0 to 3) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 6))
    }
    for (colNum <- 4 to 7) {
      dealSeq = dealSeq :+ DealStep(ElementTarget(Tableau, colNum), Payload(faceUp = true, numCards = 5))
    }
    dealSeq
  }

  val deckCon = NotConstraint(IsEmpty(Source))
  val deckDealMove:Move = DealDeckMove("DealDeck", 1,
    source=(StockContainer, deckCon), target=Some((Tableau, Truth)))

  def setBoardState: Seq[Java] = {
    Seq(Java(
      s"""
         |
         |Stack movingCards = new Stack();
         |   for (int rank = Card.KING; rank >= Card.ACE; rank--) {
         |       movingCards.add(new Card(rank, Card.CLUBS));
         |   }
         |
         |game.tableau[1].removeAll();
         |game.tableau[2].removeAll();
         |
       """.stripMargin))}

  val simplevar:Solitaire = {
    Solitaire(name = "Simplevar",
      structure = structureMap,
      layout = Layout(map),
      deal = getDeal,
      specializedElements = Seq.empty,
      moves = Seq(tableauToTableauMove, tableauToFoundationMove, deckDealMove),
      logic = BoardState(Map(Foundation -> 52)),
      solvable = false,
      testSetup = setBoardState
    )
  }
}