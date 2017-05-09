@(RootPackage: Name)
/** Must be the CardView widget. */
CardView cardView = (CardView) w;
Card theCard = (Card) cardView.getModelElement();

/** Recover the From Pile */
PileView fromPileView = (PileView) c.getDragSource();
Pile fromPile = (Pile) fromPileView.getModelElement();

// Determine the To Pile
Pile toPile = (Pile) src.getModelElement();

// Try to make the move
Move m = new @{Java(RootPackage)}.moves.MovePileCardPile (fromPile, theCard, toPile);
if (m.doMove (theGame)) {
   // SUCCESS
   theGame.pushMove (m);
} else {
  // invalid move! Return to the pile from whence it came.
  // Rely on the ability of each Widget to support this method.
  fromPileView.returnWidget (cardView);
}

// Since we could be released over a widget, or over the container, 
// we must repaint everything to be safe
c.repaint(); 
