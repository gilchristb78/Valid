package domain.constraints;

import domain.Constraint;

/**
 * This constraint checks that the given stack contains exactly one card.
 */
public class IsSingle extends Constraint {

    public final MoveInformation element;

    public IsSingle(MoveInformation element) {
        this.element = element;
    }
}
