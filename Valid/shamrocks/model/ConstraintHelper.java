package org.combinators.solitaire.shamrocks.model;

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

    public static Stack[] foundation(Solitaire game) {
        return getVariation(game).foundation;
    }

    public static Stack deck(Solitaire game) {
        return getVariation(game).deck;
    }

    public static boolean isFromMiddleMove = false;

    public static Move fromMiddleMove = null;

    public static int fromMiddleIndex = 0;

    public static boolean maxSizeExceeded(Card moving, Stack destination, int max) {
        return destination.count() < max;
    }

    /**
     * Helper to be able to retrieve variation specific solitaire without external cast.
     */
    public static org.combinators.solitaire.shamrocks.Shamrocks getVariation(Solitaire game) {
        return (org.combinators.solitaire.shamrocks.Shamrocks) game;
    }
}
