package org.combinators.solitaire.giant.model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to another.
 */
public class FlipCard extends Move {

    protected Stack destination;

    protected Stack source;

    public FlipCard(Stack from, Stack to) {
        super();
        this.source = from;
        this.destination = to;
    }

    // Extra fields, methods and constructors brought in here
    public FlipCard(Stack from) {
        this(from, from);
    }

    /**
     * Request the undo of a move.
     *
     * @param theGame ks.games.Solitaire
     */
    public boolean undo(Solitaire game) {
        Card c = source.get();
        c.setFaceUp(!c.isFaceUp());
        source.add(c);
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
        Card c = source.get();
        c.setFaceUp(!c.isFaceUp());
        source.add(c);
        return true;
    }

    /**
     * Validate the move.
     *
     * @see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {
        return (!(source.empty()) && !(source.peek().isFaceUp()));
    }
}
