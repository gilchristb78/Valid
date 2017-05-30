package domain.constraints;

import domain.ConstraintExpr;

/**
 *  Used to easily grow AndConstraints together 
 */
public class UnaryAndConstraint extends ConstraintExpr {
    ConstraintExpr c1;

    public UnaryAndConstraint (ConstraintExpr c1) {
        super();
        this.c1 = c1;
    }

    public ConstraintExpr getC1() {
        return c1;
    }

    /** Consumes the UnaryAndConstraint and returns an Add. */
    public AndConstraint add(ConstraintExpr c) {
      return new AndConstraint(c1, c);     
    }

}
