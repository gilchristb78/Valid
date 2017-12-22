package domain;

import domain.constraints.AndConstraint;
import domain.constraints.Truth;

import java.util.Iterator;
import java.util.Optional;

/**
 * A Move represents a potential move in a solitaire game. 
 *
 * A Move consists of two separate concepts.  First there is the 
 * logical construct defining a source Element, the target Element, and
 * constraints/properties on the card(s) to be allowed to move between
 * them.
 *  
 * Domain modeling captures the semantic meaning of the moves, but relies
 * on regular programming to turn the logic into actual statements. For 
 * example, you can record that "a column of cards can be moved if the
 * column is descending in rank and contains alternating colors" but you 
 * don't have to actually complete this logic in the domain model. In this
 * regard, the domain model is truly an analysis document.
 *
 * While we could use some object-oriented modeling tool/language that 
 * includes multiple inheritance, for simplicity we choose a 
 * single-inheritance style because we use java to represent the model. 
 * Certainly, one could use a more complicated domain model (i.e., EMF)
 * and that would be a reasonable alternative to pursue.
 *
 * Moves are associated dynamically with domain model elements, which 
 * allows each to vary independently as needed to model the domain.
 *
 * Moves can be associated with individual elements or with an entire
 * container, which is a sort of short-cut to specifying each of the
 * available moves.
 * 
 * TODO: Create two sets of constraints (sourceConstraint for applicability
 * TODO: on the source, and targetConstraint for applicability on the target).
 * TODO: The source constraint would be used to synthesize press controllers
 * TODO: The target constraint would be used to synthesize release controllers
 * TODO: Moves with no target would be press controller logic
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

    Element getSource();
    Element getTarget();

    Container getSourceContainer();
    Optional<Container> getTargetContainer();

    Constraint getSourceConstraint();
    Constraint getTargetConstraint();
}