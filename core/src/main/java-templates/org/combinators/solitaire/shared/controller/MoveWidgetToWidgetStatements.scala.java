@(RootPackage: Name,
        TheMove: SimpleName,
        MovingWidgetName: SimpleName,
        SourceWidgetName: SimpleName,
        TargetWidgetName: SimpleName)

@Java(MovingWidgetName) movingElement = (@Java(MovingWidgetName)) w.getModelElement();

try {
	// Safety Check
	if (movingElement==null){return;}

	// Get sourceWidget for card being dragged
	Widget sourceWidget=theGame.getContainer().getDragSource();

	// Safety Check
	if (sourceWidget==null){return;}

	@Java(TargetWidgetName) toElement=(@Java(TargetWidgetName))src.getModelElement();

	// Identify the source
	@Java(SourceWidgetName) sourceEntity = (@Java(SourceWidgetName))sourceWidget.getModelElement();

	// this is the actual move
	Move m = new @{Java(TheMove)}(sourceEntity,movingElement,toElement);

	if (m.valid(theGame)){
		m.doMove(theGame);
		theGame.pushMove(m);
	} else {
		sourceWidget.returnWidget(w);
	}
} catch (ClassCastException cce) {
	// silently ignore classCastException since that is a sign of
	// ordering issues with regards to multiple releases
}
