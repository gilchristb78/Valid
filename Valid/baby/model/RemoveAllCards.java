package org.combinators.solitaire.baby.model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to another.
 */
public class RemoveAllCards extends Move {

    protected Stack destination;

    protected Stack source;

    public RemoveAllCards(Stack from, Stack to) {
        super();
        this.source = from;
        this.destination = to;
    }

    // Extra fields, methods and constructors brought in here
    java.util.ArrayList<Card> removedCards = new java.util.ArrayList<Card>();

    public RemoveAllCards(Stack dests) {
        this(null, dests);
    }

    /**
     * Request the undo of a move.
     *
     * @param theGame ks.games.Solitaire
     */
    public boolean undo(Solitaire game) {
        while (!removedCards.isEmpty()) {
            destination.add(removedCards.remove(0));
        }
        return true;
    }

    /**
     * Execute the move.
     *
     * @see ks.common.model.Move#doMove(ks.games.Solitaire)
     */
    public boolean doMove(Solitaire game) {
        if (!valid(game)) {
            return false;
        }
        while (!destination.empty()) {
            removedCards.add(destination.get());
        }
        return true;
    }

    /**
     * Validate the move.
     *
     * @see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {
        return ((destination.descending() && destination.peek().getRank() == Card.ACE) && destination.peek(0).getRank() == Card.KING);
    }
}
