package org.combinators.solitaire.egyptian.controller;

import org.combinators.solitaire.egyptian.*;
// where move classes are placed.
import org.combinators.solitaire.egyptian.model.*;
import java.awt.event.MouseEvent;
import ks.common.model.*;
import ks.common.view.*;
import ks.common.games.*;
import ks.common.controller.*;

public class ColumnController extends SolitaireReleasedAdapter {

    protected Egyptian theGame;

    /**
     * The View being controlled
     */
    protected ColumnView src;

    public ColumnController(Egyptian theGame, ColumnView src) {
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
        Column srcElement = (Column) src.getModelElement();
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
        if (w instanceof ColumnView) {
            Column movingElement = (Column) w.getModelElement();
            try {
                // Safety Check
                if (movingElement == null) {
                    return;
                }
                // Get sourceWidget for card being dragged
                Widget sourceWidget = theGame.getContainer().getDragSource();
                // Safety Check
                if (sourceWidget == null) {
                    return;
                }
                Column toElement = (Column) src.getModelElement();
                // Identify the source
                Column sourceEntity = (Column) sourceWidget.getModelElement();
                // this is the actual move
                Move m = new MoveColumn(sourceEntity, movingElement, toElement);
                if (m.valid(theGame)) {
                    m.doMove(theGame);
                    theGame.pushMove(m);
                } else {
                    sourceWidget.returnWidget(w);
                }
            } catch (ClassCastException cce) {
            // silently ignore classCastException since that is a sign of
            // ordering issues with regards to multiple releases
            }
        }
        // release the dragging object and refresh display
        c.releaseDraggingObject();
        c.repaint();
    }
}
