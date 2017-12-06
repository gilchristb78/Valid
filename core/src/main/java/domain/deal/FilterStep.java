package domain.deal;

import domain.Constraint;

/**
 * When applied to the whole deck, remove cards from the deck which match the given filter
 * and place at the end. Process a maximum of limit cards in this fashion.
 */
public class FilterStep implements Step {
    public final Constraint constraint;
    public final int limit;

    /** Will apply given constraint to a specific card. */
    public FilterStep(Constraint cons) {
        this.constraint = cons;
        this.limit = -1;
    }

    public FilterStep(Constraint cons, int limit) {
        this.constraint = cons;
        this.limit = limit;
    }
}