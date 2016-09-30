@(WidgetVariable: NameExpr, IgnoreWidgetVariable: NameExpr)

// should we ignore this 
@Java(IgnoreWidgetVariable) = true;
@Java(WidgetVariable) = null;

// must both define me_ignore to false and set me_widget to valid widget
	// Could be something! Verify that the user clicked on the TOP card in the Column.
// Note that this method will alter the model for columnView if the condition is met.
CardView cardView = src.getCardViewForTopCard(me);
if (cardView != null) {
	@Java(WidgetVariable) = cardView;
	@Java(IgnoreWidgetVariable) = false;
}