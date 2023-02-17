package org.combinators.solitaire.fan.model;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Move element from one stack to another.
 *
 * Parameters:
 * RootPackage
 * Designate
 * DraggingCard
 */
public class PotentialMoveCardFoundation extends MoveCardFoundation {

    /**
     * Destination.
     */
    public PotentialMoveCardFoundation(Stack from, Stack to) {
        super(from, to);
    }

    @Override
    public boolean valid(Solitaire game) {
        if (movingCard == null) {
            if (source.empty()) {
                return false;
            }
            synchronized (this) {
                movingCard = source.get();
                boolean result = super.valid(game);
                source.add(movingCard);
                return result;
            }
        } else {
            return super.valid(game);
        }
    }

    @Override
    public boolean doMove(Solitaire game) {
        if (!valid(game)) {
            return false;
        }
        synchronized (this) {
            movingCard = source.get();
            boolean result = super.doMove(game);
            return result;
        }
    }
}
