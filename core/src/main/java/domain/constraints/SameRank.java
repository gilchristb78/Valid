package domain.constraints;

import domain.Constraint;

/**
 * Do two elements have the same rank.
 */
public class SameRank extends Constraint {
    public final MoveInformation left;
    public final MoveInformation right;

    public SameRank (MoveInformation left, MoveInformation right) {
        this.left = left;
        this.right =right;
    }
}
