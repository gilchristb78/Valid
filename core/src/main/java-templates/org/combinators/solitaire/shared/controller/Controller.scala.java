@(RootPackage: Name,
   Designate: SimpleName,
   NameOfTheGame : SimpleName,
   AutoMoves : Seq[Statement],
   MouseClicked: Seq[Statement],
   MousePressed: (SimpleName, SimpleName) => Seq[Statement],
   MouseReleased: Seq[Statement])
package @{Java(RootPackage)}.controller;

import @{Java(RootPackage)}.*;
import @{Java(RootPackage)}.model.*;    // where move classes are placed.
import java.awt.event.MouseEvent;
import ks.common.model.*;
import ks.common.view.*;
import ks.common.games.*;
import ks.common.controller.*;

public class @{Java(Designate)}Controller extends SolitaireReleasedAdapter {
	protected @Java(NameOfTheGame) theGame;

	/** The View being controlled */
	protected @{Java(Designate)}View src;

	public @{Java(Designate)}Controller(@Java(NameOfTheGame) theGame, @{Java(Designate)}View src) {
		super(theGame);

		this.theGame = theGame;
		this.src = src;
	}

	public void mouseClicked(MouseEvent me) {
		@Java(MouseClicked)
	}

	public void mousePressed(MouseEvent me) {
	    Container c = theGame.getContainer();

		// Another Safety Check
		Widget w = c.getActiveDraggingObject();
		if (w != Container.getNothingBeingDragged()) {
			System.err.println("mousePressed: Unexpectedly encountered a Dragging Object during a Mouse press.");
			return;
		}

		// should we ignore this
		boolean me_ignore = true;
		Widget me_widget = null;

		// must both define me_ignore to false and set me_widget to valid widget
		@Java(MousePressed(Java("me_widget").simpleName(), Java("me_ignore").simpleName()))

		if (me_ignore) {
			return;
		}

		// We tell the container what item is being dragged (and where in the Widget it was clicked)...
		c.setActiveDraggingObject(me_widget, me);

		// and where it came from
		c.setDragSource(src);

		c.repaint();
	}

	public void mouseReleased(MouseEvent me) {
		Container c = theGame.getContainer();

		// Safety Check
		Widget w = c.getActiveDraggingObject();
		if (w == Container.getNothingBeingDragged()) {
			return;
		}

		@Java(MouseReleased)

		@Java(AutoMoves)

		// release the dragging object (this will reset container's dragSource).
		c.releaseDraggingObject();

		c.repaint();
	}
}
