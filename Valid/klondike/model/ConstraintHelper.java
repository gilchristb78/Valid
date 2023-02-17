package org.combinators.solitaire.klondike.model;

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

    public static Stack[] waste(Solitaire game) {
        return getVariation(game).waste;
    }

    public static Stack[] foundation(Solitaire game) {
        return getVariation(game).foundation;
    }

    public static Stack deck(Solitaire game) {
        return getVariation(game).deck;
    }

    static int numDeals = 1;

    public static void takeRedeal() {
        numDeals--;
    }

    // if undo were allowed, we could have undoRedeal() method...
    public static boolean redealsAllowed() {
        return (numDeals != 0);
    }

    /**
     * Helper to be able to retrieve variation specific solitaire without external cast.
     */
    public static org.combinators.solitaire.klondike.Klondike getVariation(Solitaire game) {
        return (org.combinators.solitaire.klondike.Klondike) game;
    }
}
