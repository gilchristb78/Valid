package domain.constraints;

import domain.Constraint;

/**
 * Constrain element e1 to be of higher rank than element e2.
 */
public class HigherRank extends Constraint {

    public final MoveInformation higher;
    public final MoveInformation lower;

    public HigherRank(MoveInformation higher, MoveInformation lower) {
        this.higher = higher;
        this.lower = lower;
    }
}