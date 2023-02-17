package org.combinators.solitaire.idiot.model;

import ks.common.games.Solitaire;
import ks.common.model.*;
import java.util.function.BooleanSupplier;

public class ConstraintHelper {

    /**
     * Helper method for processing constraints. Uses BooleanSupplier
     * to avoid evaluating all constraints which would lead to exceptions.
     * These are now lazily evaluated.
     */
    public static boolean ifCompute(boolean guard, BooleanSupplier truth, BooleanSupplier falsehood) {
        if (guard) {
            return truth.getAsBoolean();
        } else {
            return falsehood.getAsBoolean();
        }
    }

    /**
     * Extra solitaire-manipulating methods are inserted here.
     */
    public static Stack[] tableau(Solitaire game) {
        return getVariation(game).tableau;
    }

    public static Stack deck(Solitaire game) {
        return getVariation(game).deck;
    }

    public static boolean higher(Solitaire game, Stack source) {
        // empty columns are not eligible.
        if (source.empty()) {
            return false;
        }
        if (source.rank() == Card.ACE) {
            return false;
        }
        Stack[] tableau = tableau(game);
        for (int i = 0; i < tableau(game).length; i++) {
            // skip 'from' column and empty ones
            if (tableau[i] == source || tableau[i].empty())
                continue;
            // must be same suit
            if (tableau[i].suit() != source.suit())
                continue;
            // Note ACES handles specially.
            if (tableau[i].rank() > source.rank() || tableau[i].rank() == Card.ACE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper to be able to retrieve variation specific solitaire without external cast.
     */
    public static org.combinators.solitaire.idiot.Idiot getVariation(Solitaire game) {
        return (org.combinators.solitaire.idiot.Idiot) game;
    }
}
