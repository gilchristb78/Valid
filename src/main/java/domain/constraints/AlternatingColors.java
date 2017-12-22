package domain.constraints;

import domain.*;

/**
 * A Constraint which is based on the inherent properties of a Stack of cards,
 * which must contain cards of opposite colors.
 */
public class AlternatingColors extends Constraint {

    public MoveInformation base;

    public AlternatingColors (MoveInformation base) {
        this.base = base;
    }


}
