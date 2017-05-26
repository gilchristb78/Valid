package domain.constraints;

import domain.Constraint;

/**
 *  Used to easily grow OrConstraints together 
 */
public class UnaryOrConstraint extends Constraint {
    Constraint c1;

    public UnaryOrConstraint (Constraint c1) {
        super();
        this.c1 = c1;
    }

    public Constraint getC1() {
        return c1;
    }

    /** Consumes the UnaryAndConstraint and returns an Add. */
    public OrConstraint add(Constraint c) {
      return new OrConstraint(c1, c);     
    }

}
