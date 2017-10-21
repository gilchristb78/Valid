package domain;

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
 * Note a UnaryMove class is a simpler concept required for moves
 * initiated without need for a terminating action. This includes (for
 * example), flipping a card or dealing cards from the stock to tableau.
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
 * @author heineman
 */
public abstract class Move {

   /** Assume always a source. */
   public final Container        srcContainer;

   /** Each move has a unique name, declared by invoker. */
   public final String name;

   /** Optionally there may be a target. */
   public final Optional<Container>        targetContainer;
   public final Constraint                 constraint;

   /** Constraint for a move with no target. */
   public Move (String name, Container src, Constraint cons) {
      this.name = name;
      this.srcContainer = src;
      this.targetContainer = Optional.empty();
      this.constraint = cons;
   }

   /** Constraint for a move with (src, target) and constraint. */
   public Move (String name, Container src, Container target, Constraint cons) {
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

   /** Get the source element of this move type. */
   public final Element   getSource() {
      Iterator<Element> it = srcContainer.iterator();
      if (it == null || !it.hasNext()) { return null; }
      return it.next();
   }

   /** Get the target element of this move type. */
   public final Element   getTarget() {
      Optional<Container> opt = targetContainer;
      if (!opt.isPresent()) { return null; }

      Iterator<Element> it = opt.get().iterator();
      if (it == null || !it.hasNext()) { return null; }
      return it.next();
   }

   /** Get element being moved. */
   public abstract Element   getMovableElement();

   /** Determine if single card being moved at a time. */
   public abstract boolean isSingleCardMove();

   /** Determine if single destination, or whether moved to all elements in the destination. */
   public abstract boolean isSingleDestination();
}
