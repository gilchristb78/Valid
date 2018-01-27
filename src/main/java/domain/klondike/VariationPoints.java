package domain.klondike;

import domain.Constraint;
import domain.constraints.*;

interface VariationPoints {
    /** Number of cards to deal from stock (default to 1). */
    int numToDeal();

    /** Can one reset the deck. [might turn into counter]*/
    boolean canResetDeck();

    /** Determines the logic of moving given entity to MoveComponents.Destination */
    Constraint buildOnTableau(MoveInformation bottom);

    /** Determines the logic of moving given entity to an empty Tableau. */
    Constraint buildOnEmptyTableau(MoveInformation bottom);
}
