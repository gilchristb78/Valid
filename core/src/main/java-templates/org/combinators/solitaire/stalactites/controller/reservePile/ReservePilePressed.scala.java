@(WidgetVariable: NameExpr, IgnoreWidgetVariable: NameExpr)

@Java(IgnoreWidgetVariable) = true;

Pile srcPile = (Pile) src.getModelElement();
//the column on which the mouse has pressed

//Return in the case that the column clicked on is empty
if (srcPile.count() == 0) {
	return;
}

CardView cardView = src.getCardViewForTopCard(me);
if (cardView != null) {
	@Java(WidgetVariable) = cardView;
	@Java(IgnoreWidgetVariable) = false;
}
