package domain.constraints;

import domain.*;

/**
 * A Constraint which is based on two elements having opposite color.
 */
public class OppositeColor extends Constraint {

    public final MoveInformation left;
    public final MoveInformation right;

    public OppositeColor (MoveInformation left, MoveInformation right) {
        this.left = left;
        this.right= right;
    }
}