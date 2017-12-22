package domain.narcotic;

import domain.Constraint;
import domain.constraints.MoveInformation;

/**
 *  To be used in Narcotic; all top cards in the Tableau have same rank
 */
public class AllSameRank extends Constraint {

    public final MoveInformation tableau;

    public AllSameRank(MoveInformation tableau) {
        this.tableau = tableau;
    }
}
