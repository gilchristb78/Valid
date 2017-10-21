package domain.constraints;

import domain.Constraint;

/**
 * Do two elements have the same suit.
 */
public class SameSuit extends Constraint {
    public final MoveInformation left;
    public final MoveInformation right;

    public SameSuit (MoveInformation left, MoveInformation right) {
        this.left = left;
        this.right= right;
    }
}
