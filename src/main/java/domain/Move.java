package domain;

import java.util.Optional;

/**
 * A Move is the top-level interface that represents a potential move in
 * a solitaire game.
 *
 * A Move consists of two separate concepts.  First there is the 
 * logical construct defining a source Element, the target Element, and
 * constraints/properties on the card(s) to be allowed to move between
 * them.
 *  
 * The ActualMove class provides the default behaviors of this interface.
 * All Move subclasses should subclass ActualMove rather than simply implement
 * the Move interface. The only exception is EnhancedMove.
 *
 * TODO: Move might also be useful to have placeholder for extra statements to
 * TODO: Execute (both during move and during undo, which makes this complex).
 * TODO: Think of stalactites and ability to fix the orientation during game play.
 */
public interface Move {
    /** Does this move involve just a single card, or multiple cards. */
    boolean isSingleCardMove();

    /** By definition, will only be moved to a specific destination. */
    boolean isSingleDestination();

    /** Get element being moved. */
    Element getMovableElement();

    /** Every move has a name. */
    String getName();

    /** Every move has constraints associated with it. */
    Constraint constraints();

    /** Element used to construct the Source of the move, if one exists. */
    Element getSource();

    /**
     * Element used to construct the Target, if one exists.
     * Is not protected by Optional, because that is taken care of by
     * the #getTargetContainer invocation.
     */
    Element getTarget();

    /** Retrieve the container of the source move. */
    Container getSourceContainer();

    /** If exists, returns container which is the potential target of the move. */
    Optional<Container> getTargetContainer();

    /** Returns the individual constraint associated with whether the move can be started from source. */
    Constraint getSourceConstraint();

    /** Returns the individual constraint associated with whether the move can be completed at the target. */
    Constraint getTargetConstraint();

    /**
     * Deny this move from happening by setting src/tgt constraints to Falsehood. This is the
     * only consistent way to remove a move.
     *
     * TODO: Add methods to replaceSourceConstraint(cons) and replaceTargetConstraint(cons)
     */
    void prevent();

}