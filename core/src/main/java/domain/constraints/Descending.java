package domain.constraints;

import domain.*;

/**
 * A Constraint which is based on the inherent properties of a Stack of cards,
 * which must contain cards of descending ranks (by one).
 */
public class Descending extends UnaryConstraintExpr {

    public Descending (String element) {
        super(element);
    }
}
