package domain.moves;

import domain.*;

/**
 * Flip a single card which had been face down.
 */
public class FlipCardMove extends ActualMove {

    public FlipCardMove(String name, Container src, Constraint srcCons) {
        super(name,src, srcCons);
    }

    /** By definition only a single card being moved. */
    @Override
    public boolean isSingleCardMove() { return true; }

    /** By definition, only one target affected. */
    public boolean isSingleDestination() { return true; }

    /** Not sure if necessary anymore. */
    public Element   getMovableElement() {
        return new Card();
    }
}
