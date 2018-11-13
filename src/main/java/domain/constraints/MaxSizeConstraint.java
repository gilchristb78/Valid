package domain.constraints;

import domain.Constraint;
import domain.constraints.movetypes.MoveComponents;

public class MaxSizeConstraint extends Constraint {
    public final MoveInformation movingCards;
    public final MoveInformation destination;
    public final int maxSize;

    public MaxSizeConstraint (MoveInformation movingCards, MoveInformation destination, int maxSize) {
        this.movingCards = movingCards;
        this.destination =destination;
        this.maxSize = maxSize;
    }
}
