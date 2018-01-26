package domain.moves;

import domain.Card;
import domain.Constraint;
import domain.Container;
import domain.Element;
import domain.constraints.Truth;

/**
 * A number of cards are dealt from the Stock one at a time
 * to multiple destinations.
 */
public class DeckDealNCardsMove extends DeckDealMove {

    public final int numToDeal;

    /**
     * Determine conditions for moving column of cards from src to target.
     */
    public DeckDealNCardsMove(int numToDeal, String name, Container src, Constraint srcCons, Container target) {
        super(name, src, srcCons, target);
        this.numToDeal = numToDeal;
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
