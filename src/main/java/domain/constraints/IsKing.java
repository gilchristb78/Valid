package domain.constraints;

import domain.Constraint;

/**
 * Determines if card is a king.
 */
public class IsKing extends Constraint {

    public final MoveInformation element;

    public IsKing(MoveInformation element) {
        this.element = element;
    }

}
