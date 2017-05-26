package domain.constraints;

import domain.Constraint;

/**
 *  Used to easily grow AndConstraints together 
 */
public class UnaryAndConstraint extends Constraint {
    Constraint c1;

    public UnaryAndConstraint (Constraint c1) {
        super();
        this.c1 = c1;
    }

    public Constraint getC1() {
        return c1;
    }

    /** Consumes the UnaryAndConstraint and returns an Add. */
    public AndConstraint add(Constraint c) {
      return new AndConstraint(c1, c);     
    }

}
