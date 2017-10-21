package domain.constraints;

import domain.*;

/**
 * This constraint is (for some reason) defined with extra precision. Reason is that
 * determining the rank of a Stack uses .rank() method, while rank of a Card
 * uses .getRank() method.
 */
public class IsAce extends Constraint {

    public final MoveInformation element;

    public IsAce (MoveInformation element) {
        this.element = element;
    }

}
