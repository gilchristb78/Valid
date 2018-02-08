package domain.freeCell;

import domain.Constraint;
import domain.constraints.MoveInformation;

public interface VariationPoints {

    /** Determines the logic of moving given entity to an empty Tableau. */
    Constraint buildOnEmptyTableau(MoveInformation card);

    /**
     * Determines which cards to place on foundation.
     *
     * Used by variations: Stalactites
     */
    Constraint buildOnFoundation(MoveInformation card);

}
