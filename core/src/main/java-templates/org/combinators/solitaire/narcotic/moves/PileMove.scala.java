@(RootPackage: NameExpr, PileToPileCondition: Seq[Statement])
package @{Java(RootPackage)}.moves;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Move card between piles
 */
public class MovePileCardPile extends ks.common.model.Move {

    protected Pile source;
    protected Card card;
    protected Pile target;

    /**
     * Moves card from one pile to another.
     */
    public MovePileCardPile(Pile from, Card object, Pile to) {
        super();

        this.source = from;
        this.card = object;
        this.target = to;
    }

    /**
     * To undo this move, we move the cards from top of the piles back to the deck
     */
    public boolean undo(ks.common.games.Solitaire game) {

        // move back
        source.add(target.get());
        return true;
    }

    /**
     * Execute the move
     * @@see ks.common.model.Move#doMove(ks.games.Solitaire)
     */
    public boolean doMove(Solitaire game) {
        if (!valid (game)) {
            return false;
        }

        // EXECUTE: When card is null, this move is a projection of a move. When not-null, the player
        // has already grabbed a card
        if (card == null) {
            target.add(source.get());
        } else {
            target.add(card);
        }
        return true;
    }

    /**
     * Determines whether pile1 is to the left of pile2
     */
    protected boolean toLeftOf(Pile pile1, Pile pile2) {
        String pile1Name = pile1.getName();
        String pile2Name = pile2.getName();

        int rc = pile1Name.compareTo (pile2Name);

        // We must be to the left
        if (rc < 0) return true;

        // all other cases return false.
        return false;
    }

    /**
     * Validate the move.
     * @@see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {
        boolean validation = false;

        @Java(PileToPileCondition)

        return validation;
    }
}