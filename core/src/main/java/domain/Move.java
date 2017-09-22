package domain;

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
 * container, which is a sort of short-cut to specifying each of he
 * available moves.
 * 
 * Note a UnaryMove class is a simpler concept required for moves
 * initiated without need for a terminating action. This includes (for
 * example), flipping a card or dealing cards from the stock to tableau.
 * 
 * @author heineman
 */
public abstract class Move {

   /** Assume always a source. */
   public final Container        srcContainer;

   /** Each move has a unique name, declared by invoker. */
   public final String name;

   /** Optionally there may be a target. */
   public final Optional<Container>        targetContainer;
   public final ConstraintStmt   constraint;
 
   /** Constraint for a move with no target. */
   public Move (String name, Container src, ConstraintStmt cons) {
      this.name = name;
      this.srcContainer = src;
      this.targetContainer = Optional.empty();
      this.constraint = cons;
   }

   /** Constraint for a move with (src, target) and constraint. */
   public Move (String name, Container src, Container target, ConstraintStmt cons) {
      this.name = name;
      this.srcContainer = src;
      this.targetContainer = Optional.of(target);
      this.constraint = cons;
   }

   public String toString() {
      return srcContainer + " -> " + targetContainer;
   }

   /** Return name of move. */
   public String getName() {
     return name;
   }

   /** Extract constraint associated with move. */
   public ConstraintStmt getConstraint() { return constraint; }

   /** Get container. */
   public Container getSourceContainer() { return srcContainer; }

   /** Get container for the target. */
   public Optional<Container> getTargetContainer() { 
	return targetContainer; 
   }

   /** Get the source element of this move type. */
   public abstract Element   getSource();

   /** Get the target element of this move type. */
   public abstract Element   getTarget();

   /** Get element being moved. */
   public abstract Element   getMovableElement(); 
}
