package domain.spider;

import domain.Constraint;
import domain.constraints.MoveInformation;

/**
 *  To be used in spider; all cards in the column have the same suit
 */
//should this be moved to the more general group of constraints?
public class AllSameSuit extends Constraint {

    public final MoveInformation base;

    public AllSameSuit(MoveInformation base) {
        this.base = base;
    }
}

