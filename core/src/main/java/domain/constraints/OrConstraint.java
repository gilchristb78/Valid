package domain.constraints;

import domain.*;

/**
 * If c1 is not valid, then c2 must be valid
 */
public class OrConstraint extends Constraint {
    public final Constraint[] constraints;

    public OrConstraint (Constraint... cons) {
        this.constraints = cons;
    }

    /** Debugging purposes only. */
    public String toString() {
        String total = "";

        // done in reverse, inside-out order.
        for (Constraint c : constraints) {
            total = c.toString() + "," + total;
        }
        return "Or(" + total + ")";
    }
}
