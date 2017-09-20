package domain.constraints;

import domain.ConstraintExpr;

/**
 * A Constraint which is based on the inherent properties of a single element.
 */
public abstract class UnaryConstraintExpr extends ConstraintExpr {

    /** Reference to the element. */
    final String element;

    /** Applies to a single element (could be a card, or a column). */
    public UnaryConstraintExpr(String element) {
        super();
        this.element = element;
    }

    /** Return the element being constrained. */
    public String getElement() { return element; }

}
