package domain.constraints;

import domain.Constraint;

/**
 * If c1 is not valid, then c2 must be valid
 */
public class AndConstraint extends Constraint {
    public final Constraint[] constraints;

    public AndConstraint (Constraint... cons) {
        this.constraints = cons;
    }
}
