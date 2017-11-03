package domain.moves;

import domain.*;

/**
 * A Column of cards to be moved from a source to a target.
 *
 * This will typically entail constraints on both the source and the target.
 */
public class ColumnMove extends Move {

    /**
     * Determine conditions for moving column of cards from src to target.
     */
    public ColumnMove (String name, Container src, Constraint srcCons, Container target, Constraint tgtCons) {
        super(name, src, srcCons, target, tgtCons);
    }

    /** By definition will allow multiple cards to be moved. */
    @Override
    public boolean isSingleCardMove() { return false; }

    /** By definition, will only be moved to a specific destination. */
    public boolean isSingleDestination() { return true; }

    /** Get element being moved. Hack to make work for FreeCell. */
    public Element   getMovableElement() {
        return new Column();
    }
}
