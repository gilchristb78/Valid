package domain.narcotic;

import domain.Constraint;
import domain.constraints.MoveInformation;

/**
 *  To be used in Narcotic for pile that is to left of another pile.
 *
 *  ToLeftOf(other, src) where 'src' is origination of move, and other is to its left in tableau.
 */
public class ToLeftOf extends Constraint {

    public final MoveInformation src;
    public final MoveInformation destination;

    public ToLeftOf(MoveInformation destination, MoveInformation src) {
        this.destination = destination;
        this.src = src;
    }
}
