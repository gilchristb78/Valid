package domain.moves;

import domain.*;

import java.util.Iterator;
import java.util.Optional;

/**
 * Flip a single card which had been face down.
 */
public class FlipCardMove extends Move {

    public FlipCardMove(String name, Container src, Constraint constraint) {
        super(name,src, constraint);
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
