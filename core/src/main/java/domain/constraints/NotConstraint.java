package domain.constraints;

import domain.Constraint;

/**
 * If c1 is not valid, return true (and v.v.)
 */
public class NotConstraint extends Constraint {
    public final Constraint constraint;

    public NotConstraint(Constraint cons) {
        this.constraint = cons;
    }

    /** Debugging purposes only. */
    public String toString() {
        return "Not(" + constraint.toString() + ")";
    }
}
