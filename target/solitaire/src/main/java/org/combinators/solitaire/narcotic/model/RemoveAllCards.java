package org.combinators.solitaire.narcotic.model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to a number of other stacks.
 */
public class RemoveAllCards extends ks.common.model.Move {

    /**
     * Destination.
     */
    protected Stack[] destinations;

    /**
     * Source.
     */
    protected Stack source;

    public RemoveAllCards(Stack from, Stack[] to) {
        super();
        this.source = from;
        this.destinations = to;
    }

    // helper methods go here...
    // but also additional fields...
    // but also additional constructors...
    java.util.ArrayList<Card> removedCards = new java.util.ArrayList<Card>();

    public RemoveAllCards(Stack[] dests) {
        this(null, dests);
    }

    /**
     * Request the undo of a move.
     *
     * @param theGame ks.games.Solitaire
     */
    public boolean undo(ks.common.games.Solitaire game) {
        // move back
        for (Stack s : destinations) {
            s.add(removedCards.remove(0));
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
        for (Stack s : destinations) {
            removedCards.add(s.get());
        }
        return true;
    }

    /**
     * Validate the move.
     *
     * @see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {
        return ((org.combinators.solitaire.narcotic.Narcotic) game).allSameRank(ConstraintHelper.tableau(game));
    }
}
