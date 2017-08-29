@(RootPackage: Name, NumPiles: Expression)
package @{Java(RootPackage)}.model;

import ks.common.games.*;
import ks.common.model.*;


/**
 * Represents the reset of the deck.
 */
public class MoveRemoveCards extends ks.common.model.Move {

    /** Piles. */
    protected Pile[] piles;
    protected Card[] removed;

    public MoveRemoveCards(Pile[] piles) {
        super();

        this.piles = piles;
        this.removed = new Card[piles.length];
    }

    /**
     * Each move should knows how to execute itself.
     */
    public boolean doMove (Solitaire theGame) {

        // VALIDATE:
        if (valid (theGame) == false)
            return false;

        // EXECUTE:
        for (int i = 0; i < @Java(NumPiles); i++) {
            removed[i] = piles[i].get();
        }

        // finally update the total number.
        theGame.updateScore(removed.length);
        return true;
    }
    /**
     * Undo move.
     */
    public boolean undo(ks.common.games.Solitaire theGame) {
        theGame.updateScore(-removed.length);
        for (int i = 0; i < @Java(NumPiles); i++) {
            piles[i].add(removed[i]);
            removed[i] = null;
        }

        return true;
    }

    /**
     * Validate Move.
     * @@param game edu.wpi.cs.soltaire.games.Solitaire
     */
    public boolean valid (ks.common.games.Solitaire theGame) {
        // VALIDATION:
        if (piles[0].empty()) { return false; }

        for (int i = 1; i < @Java(NumPiles); i++) {
            if (piles[i].empty() || piles[i].rank() != piles[0].rank()) { return false; }
        }

        return true;
    }
}