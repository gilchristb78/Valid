package org.combinators.solitaire.archway.model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to another.
 */
public class ReserveToTableau extends Move {

    protected Stack destination;

    protected Stack source;

    public ReserveToTableau(Stack from, Stack to) {
        super();
        this.source = from;
        this.destination = to;
    }

    // Extra fields, methods and constructors brought in here
    Card movingCard;

    public ReserveToTableau(Stack from, Card card, Stack to) {
        this(from, to);
        this.movingCard = card;
    }

    /**
     * Request the undo of a move.
     *
     * @param theGame ks.games.Solitaire
     */
    public boolean undo(Solitaire game) {
        source.add(destination.get());
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
        destination.add(movingCard);
        return true;
    }

    /**
     * Validate the move.
     *
     * @see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {
        return (true && destination.empty());
    }
}
