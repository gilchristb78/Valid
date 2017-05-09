@(WidgetVariable: SimpleName, IgnoreWidgetVariable: SimpleName)

//cannot ignore any more
@Java(IgnoreWidgetVariable) = false;

Pile srcPile = (Pile) src.getModelElement();

// Return in the case that the pile clicked on is empty
if (srcPile.count() == 0) {
	c.releaseDraggingObject();
	return;
}

// To get the top card in the pile itself.
// Note: This method will alter the model for columnView if the condition is met.	
@Java(WidgetVariable) = src.getCardViewForTopCard(me);

// Safety Check
if (@Java(WidgetVariable) == null) {
	System.err.println("FreeCellFreeCellController::pressCardController(): Unexpectedly encountered an empty pile.");
	return;
}
