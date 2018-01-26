package domain.moves;

import domain.*;
import domain.constraints.Truth;

/**
 * One card each is dealt from the Stock to multiple destinations.
 */
public class DeckDealMove extends ActualMove {

    /**
     * Determine conditions for moving column of cards from src to target. 
     */
    public DeckDealMove (String name, Container src, Constraint srcCons, Container target) {
        super(name, src, srcCons, target, new Truth());
    }

    /** By definition will allow multiple cards to be moved. Less relevant for deck, but at least consistent. */
    @Override
    public boolean isSingleCardMove() {
        return true;
    }

    /** By definition, deal to all elements in the container. */
    public boolean isSingleDestination() { return false; }

    /**
     * Get element being moved.
     *
     * Even though no card is dragged, this is accurate.
     */
    public Element getMovableElement() {
        return new Card();
    }
}
