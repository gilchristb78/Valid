package org.combinators.solitaire.spiderette.model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to another.
 */
public class MoveCardFoundation extends Move {

    protected Stack destination;

    protected Stack source;

    public MoveCardFoundation(Stack from, Stack to) {
        super();
        this.source = from;
        this.destination = to;
    }

    // Extra fields, methods and constructors brought in here
    Stack movingCards;

    int numCards;

    public MoveCardFoundation(Stack from, Stack cards, Stack to) {
        this(from, to);
        this.movingCards = cards;
        this.numCards = cards.count();
    }

    /**
     * Request the undo of a move.
     *
     * @param theGame ks.games.Solitaire
     */
    public boolean undo(Solitaire game) {
        destination.select(numCards);
        source.push(destination.getSelected());
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
        destination.push(movingCards);
        return true;
    }

    /**
     * Validate the move.
     *
     * @see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {
        return (true && (destination.empty() && ((movingCards.descending() && ((org.combinators.solitaire.spiderette.Spiderette) game).allSameSuit(movingCards)) && (movingCards.peek().getRank() == Card.ACE && movingCards.peek(0).getRank() == Card.KING))));
    }
}
