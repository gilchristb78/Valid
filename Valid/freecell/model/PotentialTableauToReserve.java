package org.combinators.solitaire.freecell.model;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Potential Move when multiple cards in play; note that 'numCards' is inherited, and is
 * drawn from the MoveHelper combinators that created the parent Move classes in the first place.
 *
 * Parameters:
 * RootPackage
 * Designate
 * DraggingCard
 */
public class PotentialTableauToReserve extends TableauToReserve {

    /**
     * Destination.
     */
    public PotentialTableauToReserve(Stack from, Stack to) {
        super(from, to);
        // was numInColumn
        numCards = 1;
    }

    public PotentialTableauToReserve(Stack from, Stack to, int num) {
        super(from, to);
        numCards = num;
    }

    @Override
    public boolean valid(Solitaire game) {
        if (movingCards == null) {
            if (source.count() < numCards) {
                return false;
            }
            // make sure to keep order of potential column intact
            synchronized (this) {
                movingCards = new Column();
                // numInColumn
                source.select(numCards);
                movingCards.push(source.getSelected());
                boolean result = super.valid(game);
                source.push(movingCards);
                return result;
            }
        } else {
            return super.valid(game);
        }
    }

    @Override
    public boolean doMove(Solitaire game) {
        if (!valid(game)) {
            return false;
        }
        synchronized (this) {
            movingCards = new Column();
            // numInColumn
            source.select(numCards);
            movingCards.push(source.getSelected());
            boolean result = super.doMove(game);
            return result;
        }
    }
}
