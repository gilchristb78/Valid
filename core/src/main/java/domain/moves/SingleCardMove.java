package domain.moves;

import domain.*;
import java.util.*;

/**
 * A single card being moved to a single destination.
 */
public class SingleCardMove extends Move {

    /**
     * Determine conditions for moving column of cards from src to target. 
     */
    public SingleCardMove (String name, Container src, Container target, Constraint constraint) {
        super(name, src, target, constraint);
    }

    /** By definition only a single card being moved. */
    @Override
    public boolean isSingleCardMove() {
        return true;
    }

    /** By definition, just affect a single location. */
    public boolean isSingleDestination() { return true; }

    public SingleCardMove (String name, Container src, Constraint constraint) {
        super(name,src, constraint);
    }

    /** Get element being moved. Hack to make work for FreeCell. */
    public Element   getMovableElement() {
        return new Card();
    }
}
