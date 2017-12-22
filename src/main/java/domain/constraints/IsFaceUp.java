package domain.constraints;

import domain.Constraint;

/**
 * Determine if card is face up
 */
public class IsFaceUp extends Constraint {

    public final MoveInformation element;

    public IsFaceUp(MoveInformation element) {
        this.element = element;
    }

}
