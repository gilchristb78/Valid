package org.combinators.solitaire.egyptian.model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to another.
 */
public class MoveColumn extends Move {

    protected Stack destination;

    protected Stack source;

    public MoveColumn(Stack from, Stack to) {
        super();
        this.source = from;
        this.destination = to;
    }

    // Extra fields, methods and constructors brought in here
    Stack movingCards;

    int numCards;

    public MoveColumn(Stack from, Stack cards, Stack to) {
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
        return (true && (((destination.empty() && movingCards.descending()) && movingCards.alternatingColors()) || ((movingCards.descending() && movingCards.alternatingColors()) && destination.peek().oppositeColor(movingCards.peek(0)))));
    }
}
