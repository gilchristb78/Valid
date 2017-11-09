package domain.bigforty;

import domain.Constraint;
import domain.constraints.MoveInformation;

/**
 *  To be used in bigforty; all cards in the column have the same suit
 */
public class AllSameSuit extends Constraint {

    public final MoveInformation base;

    public AllSameSuit(MoveInformation base) {
        this.base = base;
    }
}

