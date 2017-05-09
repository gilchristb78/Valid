package domain.constraints;

import domain.*;

/**
 * If c1 is not valid, then we are valid; and vv.
 */
public class NotConstraint extends Constraint {
    Constraint c1;

    public NotConstraint (Constraint c1) {
        super();
        this.c1 = c1;
    }

    public Constraint getC1() {
        return c1;
    }
}
