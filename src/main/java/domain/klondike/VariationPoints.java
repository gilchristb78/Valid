package domain.klondike;

import domain.Constraint;
import domain.constraints.*;

public interface VariationPoints {
    /** Number of cards to deal from stock (default to 1). */
    int numToDeal();

    /** How many times can one reset the deck. 0=NEVER, -1=ALWAYS, +k=k times. */
    int numRedeals();

    /** Signals any number of redeals. */
    int INFINITE_REDEAL = -1;

    /** Never allow a redeal. */
    int NEVER_REDEAL = 0;

    /** Determines the logic of moving given entity to MoveComponents.Destination */
    Constraint buildOnTableau(MoveInformation bottom);

    /** Determines the logic of moving given entity to an empty Tableau. */
    Constraint buildOnEmptyTableau(MoveInformation bottom);
}
