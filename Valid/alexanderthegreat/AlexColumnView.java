package org.combinators.solitaire.alexanderthegreat;

import ks.common.view.*;
import java.awt.event.MouseEvent;
import org.combinators.solitaire.alexanderthegreat.model.ConstraintHelper;
import ks.common.model.Card;
import ks.common.model.Column;

public class AlexColumnView extends ColumnView {

    public AlexColumnView(AlexColumn element) {
        super(element);
    }

    public CardView getCardViewForTopCard(java.awt.event.MouseEvent me) {
        Column theColumn = (Column) getModelElement();
        int numCards = theColumn.count();
        // no chance in an empty Column!
        if (numCards == 0)
            return null;
        // No card selected
        int cardNum = setSelectedCards(me);
        if (cardNum == 0 || cardNum > 1 && ConstraintHelper.fromMiddleMove != null) {
            return null;
        }
        // Create a ColumnView widget for this card.
        // Create an empty column
        Column theDraggingColumn = new Column();
        // Add the chosen card(s) to it
        theDraggingColumn.push(theColumn.getSelected());
        // Okay, since this is for a RowView, we only _really_ want 1 card, so:
        int theDraggingColumnWidth = (cards.getOverlap() * (theDraggingColumn.count() - 1));
        Card theCard = null;
        if (theDraggingColumn.count() > 0) {
            // Get the card we want
            theCard = new Card(theDraggingColumn.peek(0));
            // return the rest
            for (int i = 1; i < theDraggingColumn.count(); i++) {
                ConstraintHelper.isFromMiddleMove = true;
                ConstraintHelper.fromMiddleIndex = theDraggingColumn.count();
                // ConstraintHelper.canSelectMiddle = false;
                theColumn.add(theDraggingColumn.peek(i));
            }
        }
        // Make a view
        CardView cv = new CardView(theCard);
        // Set Bounds of this cardView widget to the bounds of the Pile from our own coordinates.
        int theCardWidth = (cards.getOverlap() * (theColumn.count() + (theDraggingColumn.count() - 1)));
        cv.setBounds(new java.awt.Rectangle(x, y + (theCardWidth - theDraggingColumnWidth), cards.getWidth(), cards.getHeight()));
        // use the same peer. NOTE: we must do this because we aren't adding this widget. As a dynamic
        // widget, I feel it would be wrong to add this to the static list of widgets for this container.
        // Note that this means that this widget has no recourse to mouse events.
        cv.setContainer(container);
        // all set.
        return cv;
    }

    public int setSelectedCards(MouseEvent me) {
        AlexColumn theRow = (AlexColumn) getModelElement();
        double whichToSelect = 0;
        // Did they click the last card?
        if ((me.getY() - this.y) > (cards.getOverlap() * (theRow.count() - 1)) && (me.getY() - this.y) < (cards.getOverlap() * (theRow.count() - 1)) + cards.getHeight()) {
            whichToSelect = 1;
        } else {
            // Find the card (unless they clicked past the overlap width on the fully visible card)
            whichToSelect = Math.floor((me.getY() - this.y) / cards.getOverlap());
            // Don't want the green stuff
            whichToSelect = Math.min(whichToSelect, theRow.count());
            whichToSelect = theRow.count() - whichToSelect;
        }
        if (theRow.count() >= whichToSelect) {
            // As it should be from the last line
            theRow.select((int) whichToSelect);
            return (int) whichToSelect;
        } else {
            return 0;
        }
    }
}
