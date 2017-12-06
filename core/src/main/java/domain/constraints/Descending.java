package domain.constraints;

import domain.*;

/**
 * A Constraint which is based on the inherent properties of a Stack of cards,
 * which must contain cards of descending Ranks (by one).
 */
public class Descending extends Constraint {

    public final MoveInformation base;

    public Descending (MoveInformation base) {
        this.base = base;
    }
}
