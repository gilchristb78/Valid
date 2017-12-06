package domain.constraints;

import domain.*;

/**
 * Is the given target an ace?
 */
public class IsAce extends Constraint {

    public final MoveInformation element;

    public IsAce (MoveInformation element) {
        this.element = element;
    }

}
