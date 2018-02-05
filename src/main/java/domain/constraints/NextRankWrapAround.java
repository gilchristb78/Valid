package domain.constraints;

import domain.Constraint;

/**
 * Is rank of element e1 equal to 1 + rank of element 2. Wrap around, thus an Ace can be placed
 * on a King.
 */
public class NextRankWrapAround extends Constraint {

    public final MoveInformation higher;
    public final MoveInformation lower;

    public NextRankWrapAround(MoveInformation higher, MoveInformation lower) {
       this.higher = higher;
       this.lower = lower;
    }
}
