@(RootPackage: NameExpr)
/** Must be the CardView widget. */
CardView cardView = (CardView) w;
Card theCard = (Card) cardView.getModelElement();

/** Recover the From Column */
Widget fromWidget = c.getDragSource();
Column fromColumn = (Column) fromWidget.getModelElement();

// Determine the target column
Column toColumn = (Column) src.getModelElement();

// Try to make the move
Move m = new @{Java(RootPackage)}.moves.ColumnColumnMove (fromColumn, theCard, toColumn);
if (m.doMove (theGame)) {
	// SUCCESS
	theGame.pushMove (m);
} else {
  // invalid move! Return to the pile from whence it came.
  // Rely on the ability of each Widget to support this method.
	fromWidget.returnWidget (cardView);
}

// Since we could be released over a widget, or over the container, 
// we must repaint everything to be safe
c.repaint(); 
