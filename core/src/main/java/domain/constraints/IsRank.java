package domain.constraints;

import domain.Card;
import domain.Constraint;

/**
 * Is given target a given rank?
 */
public class IsRank extends Constraint {

    public final MoveInformation element;
    public final Card.Ranks rank;

    public IsRank(MoveInformation element, Card.Ranks rank) {
        this.element = element;
        this.rank = rank;
    }

}
