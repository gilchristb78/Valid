package domain.constraints;

import domain.ConstraintExpr;

/**
 *  Used to easily grow OrConstraints together 
 */
public class UnaryOrConstraint extends ConstraintExpr {
    ConstraintExpr c1;

    public UnaryOrConstraint (ConstraintExpr c1) {
        super();
        this.c1 = c1;
    }

    public ConstraintExpr getC1() {
        return c1;
    }

    /** Consumes the UnaryAndConstraint and returns an Add. */
    public OrConstraint add(ConstraintExpr c) {
      return new OrConstraint(c1, c);     
    }

}
