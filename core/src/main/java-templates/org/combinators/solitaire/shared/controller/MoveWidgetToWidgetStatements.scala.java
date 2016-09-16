@(RootPackage: NameExpr,
        TheMove: NameExpr,
        MovingWidgetName: NameExpr,
        SourceWidgetName: NameExpr,
        TargetWidgetName: NameExpr)

@Java(MovingWidgetName) movingElement = (@Java(MovingWidgetName)) w.getModelElement();

// Safety Check
if (movingElement == null) { return; }

// Get sourceWidget for card being dragged
Widget sourceWidget = theGame.getContainer().getDragSource();

// Safety Check
if (sourceWidget == null) { return; }
@Java(TargetWidgetName) toElement = (@Java(TargetWidgetName)) src.getModelElement();

// Case: Card is coming from a Column
@Java(SourceWidgetName) sourceCol = (@Java(SourceWidgetName)) sourceWidget.getModelElement();			

// this is the actual move
Move m = new @{Java(TheMove)}(sourceCol, movingElement, toElement);

if (m.valid(theGame)) {
	m.doMove(theGame);
	theGame.pushMove(m);
} else {
	sourceWidget.returnWidget(w);
}	
		