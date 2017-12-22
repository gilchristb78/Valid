package domain.moves;

import domain.*;
import domain.constraints.Truth;

/**
 * A single card being moved to a single destination.
 */
public class SingleCardMove extends ActualMove {

    /**
     * Determine conditions for moving column of cards from src to target. 
     */
    public SingleCardMove (String name, Container src, Constraint srcCons, Container target, Constraint tgtCons) {
        super(name, src, srcCons, target, tgtCons);
    }

    /** Move which has no constraints solely on the source. */
    public SingleCardMove (String name, Container src, Container target, Constraint tgtCons) {
        super(name, src, new Truth(), target, tgtCons);
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
