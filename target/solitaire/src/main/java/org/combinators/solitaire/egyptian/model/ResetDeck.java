package org.combinators.solitaire.egyptian.model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to a number of other stacks.
 */
public class ResetDeck extends ks.common.model.Move {

    /**
     * Destination.
     */
    protected Stack[] destinations;

    /**
     * Source.
     */
    protected Stack source;

    public ResetDeck(Stack from, Stack[] to) {
        super();
        this.source = from;
        this.destinations = to;
    }

    // helper methods go here...
    // but also additional fields...
    // but also additional constructors...
    static int numReset = 1;

    /**
     * Request the undo of a move.
     *
     * @param theGame ks.games.Solitaire
     */
    public boolean undo(ks.common.games.Solitaire game) {
        // move back
        return false;
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
        // be reformed into a single deck.
        for (Stack s : destinations) {
            while (!s.empty()) {
                source.add(s.get());
            }
        }
        return true;
    }

    /**
     * Validate the move.
     *
     * @see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {
        return (source.empty() && true);
    }
}
