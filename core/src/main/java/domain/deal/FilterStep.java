package domain.deal;

import domain.Constraint;

/**
 * When applied to the whole deck, remove cards from the deck which match the given filter
 * and place at the end.
 */
public class FilterStep implements Step {
    public final Constraint constraint;

    /** Will apply given constraint to a specific card. */
    public FilterStep(Constraint cons) {
        this.constraint = cons;
    }
}