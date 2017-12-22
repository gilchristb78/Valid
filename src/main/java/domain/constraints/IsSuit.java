package domain.constraints;

import domain.Card;
import domain.Constraint;

/**
 * Is given target a given suit?
 */
public class IsSuit extends Constraint {

    public final MoveInformation element;
    public final Card.Suits suit;

    public IsSuit(MoveInformation element, Card.Suits suit) {
        this.element = element;
        this.suit = suit;
    }

}
