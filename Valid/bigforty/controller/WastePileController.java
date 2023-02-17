package org.combinators.solitaire.bigforty.controller;

import org.combinators.solitaire.bigforty.*;
// where move classes are placed.
import org.combinators.solitaire.bigforty.model.*;
import java.awt.event.MouseEvent;
import ks.common.model.*;
import ks.common.view.*;
import ks.common.games.*;
import ks.common.controller.*;

public class WastePileController extends SolitaireReleasedAdapter {

    protected Bigforty theGame;

    /**
     * The View being controlled
     */
    protected WastePileView src;

    public WastePileController(Bigforty theGame, WastePileView src) {
        super(theGame);
        this.theGame = theGame;
        this.src = src;
    }

    public void mouseClicked(MouseEvent me) {
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
        // Return in the case that the widget clicked on is empty
        me_ignore = false;
        // Return in the case that the widget clicked on is empty
        WastePile srcElement = (WastePile) src.getModelElement();
        if (srcElement.count() == 0) {
            return;
        }
        me_widget = src.getCardViewForTopCard(me);
        if (me_widget == null) {
            return;
        }
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
        c.getDragSource().returnWidget(w);
        // release the dragging object and refresh display
        c.releaseDraggingObject();
        c.repaint();
    }
}
