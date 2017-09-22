@(RootPackage: Name,
        TheMove: SimpleName,
        MovingWidgetName: SimpleName,
        SourceWidgetName: SimpleName,
        TargetWidgetName: SimpleName)
package @{Java(RootPackage)}.controller;


import ks.common.model.*;
import ks.common.view.*;
import ks.common.games.*;
import @{Java(RootPackage)}.model.*;    // all moves are generated within this package

public class Move@{Java(TheMove)}@{Java(MovingWidgetName)}@{Java(SourceWidgetName)}@{Java(TargetWidgetName)} {
		 	
	public static boolean processDragging (@{Java(TargetWidgetName)}View to, Solitaire theGame, @{Java(MovingWidgetName)}View w) {
		@Java(MovingWidgetName) movingElement = (@Java(MovingWidgetName)) w.getModelElement();
		
		// Safety Check
		if (movingElement == null) { return false; }
		
		// Get sourceWidget for card being dragged
		Container c = theGame.getContainer();
		Widget sourceWidget = c.getDragSource();
		
		// Safety Check
		if (sourceWidget == null) { return false; }
		@Java(TargetWidgetName) toElement = (@Java(TargetWidgetName)) to.getModelElement();
		
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
				
		return true;
	}
}