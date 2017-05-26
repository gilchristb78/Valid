package domain.constraints;

import java.util.*;
import domain.Constraint;

/**
 * If c1 is not valid, then c2 must be valid
 */
public class AndConstraint extends Constraint {
    Constraint c1;
    Constraint c2;

    public AndConstraint (Constraint c1, Constraint c2) {
        super();
        this.c1 = c1;
        this.c2 = c2;
    }

    public static UnaryAndConstraint builder(Constraint c) {
       return new UnaryAndConstraint(c);
    } 

    /** Add AndConstraints to the right. */
    public AndConstraint add(Constraint c) {
      return new AndConstraint (c, this);
    }

    public Constraint getC1() {
        return c1;
    }

    public Constraint getC2() {
        return c2;
    }
}
