package domain.constraints;

import domain.Constraint;

/**
 * Is rank of element e1 equal to 1 + rank of element 2.
 *
 */
public class NextRank extends Constraint {

    public final MoveInformation higher;
    public final MoveInformation lower;

    public NextRank (MoveInformation higher, MoveInformation lower) {
       this.higher = higher;
       this.lower = lower;
    }
}
