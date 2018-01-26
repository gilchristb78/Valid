package domain.klondike;

import domain.Constraint;
import domain.constraints.*;

interface VariationPoints {
    /** Number of cards to deal from stock (default to 1). */
    int numToDeal();

    /** Determines the logic of moving given entity to MoveComponents.Destination */
    Constraint buildOnTableau(MoveInformation bottom);

    /** Determines the logic of moving given entity to an empty Tableau. */
    Constraint buildOnEmptyTableau(MoveInformation bottom);
}
