package domain.bigforty;

import domain.Constraint;
import domain.constraints.MoveInformation;

/**
 *  To be used in bigforty; all top cards in the Tableau have same suit
 */
public class AllSameSuit extends Constraint {

    public final MoveInformation tableau;

    public AllSameSuit(MoveInformation tableau) {
        this.tableau = tableau;
    }
}

