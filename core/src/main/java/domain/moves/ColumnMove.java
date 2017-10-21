package domain.moves;

import domain.*;
import java.util.*;

/**
 * A column of cards are allowed to be moved
 */
public class ColumnMove extends Move {

    /**
     * Determine conditions for moving column of cards from src to target.
     */
    public ColumnMove (String name, Container src, Container target, Constraint constraint) {
        super(name, src, target, constraint);
    }

    /** By definition will allow multiple cards to be moved. */
    @Override
    public boolean isSingleCardMove() {
        return false;
    }

    /** By definition, will only be moved to a specific destination. */
    public boolean isSingleDestination() { return true; }

    /** Get element being moved. Hack to make work for FreeCell. */
    public Element   getMovableElement() {
        return new Column();
    }
}
