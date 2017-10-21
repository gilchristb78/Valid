package domain.constraints.movetypes;

import domain.constraints.MoveInformation;

public enum MoveComponents implements MoveInformation {
    Source,         // Source of Move (or potential move)
    Destination,    // Destination of move (or potential move)

    MovingCard,     // when a single card is in play
    MovingColumn,   // when multiple cards are in play

}
