package domain.freeCell;

import domain.Constraint;
import domain.constraints.MoveInformation;

/**
 *  To be used in Stalactites (but might be interesting to others)
 *
 *  Determines if a Foundation Pile has thirteen cards; if so it is full
 */
public class Full extends Constraint {

    public final MoveInformation src;

    public Full(MoveInformation src){
        this.src = src;
    }
}
