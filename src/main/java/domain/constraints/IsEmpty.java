package domain.constraints;

import domain.Constraint;

/**
 * Determines whether a stack of cards is empty.
 */
public class IsEmpty extends Constraint {
    public final MoveInformation element;

    public IsEmpty(MoveInformation element) {
        this.element = element;
    }
}
