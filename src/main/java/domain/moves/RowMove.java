package domain.moves;

import domain.*;

/**
 * A Row of cards to be moved from a source to a target.
 *
 * Still uses a 'Column' of moveable elements
 */
public class RowMove extends ActualMove {

    /**
     * Determine conditions for moving column of cards from src to target.
     */
    public RowMove(String name, Container src, Constraint srcCons, Container target, Constraint tgtCons) {
        super(name, src, srcCons, target, tgtCons);
    }

    /** By definition will allow multiple cards to be moved. */
    @Override
    public boolean isSingleCardMove() { return false; }

    /** By definition, will only be moved to a specific destination. */
    public boolean isSingleDestination() { return true; }

    /** Get element being moved. */
    public Element   getMovableElement() {
        return new Column();
    }
}
