package domain.constraints;

import java.util.*;
import domain.ConstraintExpr;

/**
 * If c1 is not valid, then c2 must be valid
 */
public class AndConstraint extends ConstraintExpr {
    ConstraintExpr c1;
    ConstraintExpr c2;

    public AndConstraint (ConstraintExpr c1, ConstraintExpr c2) {
        super();
        this.c1 = c1;
        this.c2 = c2;
    }

    public static UnaryAndConstraint builder(ConstraintExpr c) {
       return new UnaryAndConstraint(c);
    } 

    /** Add AndConstraints to the right. */
    public AndConstraint add(ConstraintExpr c) {
      return new AndConstraint (c, this);
    }

    public ConstraintExpr getC1() {
        return c1;
    }

    public ConstraintExpr getC2() {
        return c2;
    }
}
