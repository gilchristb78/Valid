package domain.moves;

import domain.*;

import java.util.Iterator;
import java.util.Optional;

/**
 * Flip a single card which had been face down.
 */
public class FlipCardMove extends Move {

    /**
     * Determine conditions for moving column of cards from src to target.
     */
    public FlipCardMove(String name, Container src, Container target, ConstraintStmt constraint) {
        super(name, src, target, constraint);
    }

    public FlipCardMove(String name, Container src, ConstraintStmt constraint) {
        super(name,src, constraint);
    }

    /** By definition only a single card being moved. */
    @Override
    public boolean isSingleCardMove() { return true; }

    /** By definition, only one target affected. */
    public boolean isSingleDestination() { return true; }

    /** Extract constraint associated with move. */
    public ConstraintStmt getConstraint() { return constraint; }

    public String toString() {
        return super.toString() + " : " + constraint;
    }

    /** Get the source element of this move type. */
    public Element   getSource() {
        Iterator<Element> it = getSourceContainer().iterator();
        if (it == null || !it.hasNext()) { return null; }
        return it.next();
    }

    /** Get the target element of this move type. */
    public Element   getTarget() {
        Optional<Container> opt = getTargetContainer();
        if (!opt.isPresent()) { return null; }

        Iterator<Element> it = opt.get().iterator();
        if (it == null || !it.hasNext()) { return null; }
        return it.next();
    }

    /** Not sure if necessary anymore. */
    public Element   getMovableElement() {
        return new Card(Rank.ACE, Suit.SPADES);
    }
}
