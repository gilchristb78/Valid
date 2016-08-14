@(WidgetVariable: NameExpr, IgnoreWidgetVariable: NameExpr)
// Ask PileView to retrieve the top card as a CardView Widget
@Java(WidgetVariable) = src.getCardViewForTopCard(me);
@Java(IgnoreWidgetVariable) = false;
// we simply redraw our source pile to avoid flicker
src.redraw();