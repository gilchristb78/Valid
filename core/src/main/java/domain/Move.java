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
   public final Container srcContainer;

   /** Each move has a unique name, declared by invoker. */
   public final String name;

   /** Optionally there may be a target. Store as Optional for ease of access directly to this field. */
   public final Optional<Container>        targetContainer;

   /** There may be a valid constraint at the source of a move. */
   public final Constraint                 sourceConstraint;

   /** There may be a valid constraint at the target of a move. */
   public final Constraint                 targetConstraint;


   /**
    * When a Move only has a source, then its given constraint is the source Constraint. The
    * targetConstraint is set to Truth() and the targetContainer becomes Optional.empty
    *
    * @param name       The designated (unique) name for a move.
    * @param src        The source container of the move.
    * @param srcCons    The source constraint associated with the move.
    */
   public Move (String name, Container src, Constraint srcCons) {
      this.name = name;
      this.srcContainer = src;
      this.sourceConstraint = srcCons;

      this.targetContainer = Optional.empty();
      this.targetConstraint = new Truth();
   }

   /**
    * When a Move has a source and target, then it may have constraints for both the source and target.
    * If either is 'empty' then the caller must pass in Truth()
    *
    * @param name       The designated (unique) name for a move.
    * @param src        The source container of the move.
    * @param srcCons    The source constraint associated with the move.
    * @param target     The target container of the move.
    * @param tgtCons    The target constraint associated with the move.
    *
    */
   public Move (String name, Container src, Constraint srcCons, Container target, Constraint tgtCons) {
      this.name = name;
      this.srcContainer = src;
      this.sourceConstraint = srcCons;

      this.targetContainer = Optional.of(target);
      this.targetConstraint = tgtCons;
   }

   public String toString() {
      return srcContainer + "(" + sourceConstraint + ") -> " + targetContainer + "(" + targetConstraint + ")";
   }

   /**
    * Helper method to return combined set of src- and target-constraints.
    *
    * Note: Could optimize if detecting Truth in either src/target, then return the other one
    */
   public Constraint constraints() {
      if (targetContainer.isPresent()) {
         return new AndConstraint(sourceConstraint, targetConstraint);
      }

      return sourceConstraint;
   }

   /** Return name of move. */
   public String getName() {
     return name;
   }

   /** Get the source element of this move type. */
   public final Element   getSource() {
      Iterator<Element> it = srcContainer.iterator();
      if (!it.hasNext()) { return null; }
      return it.next();
   }

   /** Get the target element of this move type. */
   public final Element   getTarget() {
      Optional<Container> opt = targetContainer;
      if (!opt.isPresent()) { return null; }

      Iterator<Element> it = opt.get().iterator();
      if (!it.hasNext()) { return null; }
      return it.next();
   }

   /** Get element being moved. */
   public abstract Element getMovableElement();

   /** Determine if single card being moved at a time. */
   public abstract boolean isSingleCardMove();

   /** Determine if single destination, or whether moved to all elements in the destination. */
   public abstract boolean isSingleDestination();
}
