package domain.constraints;

import domain.ConstraintExpr;

/**
 * A constraint on two different elements and the order matters.
 */
public abstract class BinaryConstraintExpr extends ConstraintExpr {

    /** First element in comparison. */
    final String element1;

    /** Second element in comparison. */
    final String element2;

    /** Determine elements of comparison. */
    public BinaryConstraintExpr(String e1, String e2) {
        super();
        this.element1 = e1;
        this.element2 = e2;
    }

    public String getElement1() { return element1; }
    public String getElement2() { return element2; }
}
