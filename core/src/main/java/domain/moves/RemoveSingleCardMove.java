package domain.moves;

import domain.*;

import java.util.Iterator;
import java.util.Optional;

/**
 * Calls for the removal of a single card, from a single model element.
 *
 * Note that the src container is needed, to comply with Move superclass, but is otherwise
 * not essential.
 *
 */
public class RemoveSingleCardMove extends Move {

    /**
     * Determine conditions for removing multiple cards from container
     */
    public RemoveSingleCardMove(String name, Container src, Constraint constraint) {
        super(name, src, constraint);
    }

    /** By definition a single card. */
    @Override
    public boolean isSingleCardMove() { return true; }

    /** By definition, only affects a single element. */
    public boolean isSingleDestination() { return true; }

    /** Get element being moved. Hack to make work for FreeCell. */
    public Element   getMovableElement() {
        return new Card();
    }
}
