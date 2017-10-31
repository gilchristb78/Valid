package domain.constraints.movetypes;

import domain.constraints.MoveInformation;

public enum MoveComponents implements MoveInformation {
    Source("source"),               // Source of Move (or potential move)
    Destination("destination"),     // Destination of move (or potential move)

    MovingCard("movingCard"),       // when a single card is in play
    MovingColumn("movingColumn");   // when multiple cards are in play

    public final String name;

    MoveComponents(String name) {
        this.name = name;
    }

    public String getName() { return name; }

}
