package domain.constraints;

import domain.*;

/**
 * If c1 is not valid, then c2 must be valid
 */
public class OrConstraint extends ConstraintExpr {
    ConstraintExpr c1;
    ConstraintExpr c2;

    public OrConstraint (ConstraintExpr c1, ConstraintExpr c2) {
        super();
        this.c1 = c1;
        this.c2 = c2;
    }

    public static UnaryOrConstraint builder(ConstraintExpr c) {
       return new UnaryOrConstraint(c);
    }

    /** Add OrConstraints to the right. */
    public OrConstraint add(ConstraintExpr c) {
      return new OrConstraint (c, this);
    }

    public ConstraintExpr getC1() {
        return c1;
    }

    public ConstraintExpr getC2() {
        return c2;
    }
}
