package domain.constraints;

import domain.*;

/**
 * A Constraint which is based on two elements having opposite color.
 */
public class OppositeColor extends BinaryConstraintExpr {

    public OppositeColor (String e1, String e2) {
        super(e1, e2);
    }
}
