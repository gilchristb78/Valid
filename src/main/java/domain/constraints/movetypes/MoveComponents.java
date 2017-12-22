package domain.constraints.movetypes;

import domain.constraints.MoveInformation;

/**
 * A move is defined by a source and a destination.
 *
 * It can either specify a single card being moved, or a column of cards being moved.
 *
 * note: flipping a card from face up to face down only affects a single source and so would
 * not fall into this category.
 */
public enum MoveComponents implements MoveInformation {
    Source("source"),               // Source of Move (or potential move)
    Destination("destination"),     // Destination of move (or potential move)

    MovingCard("movingCard"),       // when a single card is in play
    MovingColumn("movingColumn"),   // when multiple cards are in play
    MovingRow("movingRow");         // when multiple cards are moved as a row. Not sure if needed...

    public final String name;

    MoveComponents(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    /** Only the MovingCard is a single card. All others are multiple. */
    public boolean isSingleCard() { return this == MovingCard; }

}
