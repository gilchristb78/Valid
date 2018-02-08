package domain.moves;

import domain.Constraint;
import domain.Container;
import domain.Element;
import domain.Move;
import domain.constraints.AndConstraint;
import domain.constraints.Falsehood;
import domain.constraints.Truth;

import java.util.Iterator;
import java.util.Optional;

/**
 * An ActualMove represents a potential move in a solitaire game.
 *
 * A Move consists of two separate concepts.  First there is the 
 * logical construct defining a source Element, the target Element, and
 * constraints/properties on the card(s) to be allowed to move between
 * them.
 *  
 * KlondikeDomain modeling captures the semantic meaning of the moves, but relies
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
 * TODO: Move might also be useful to have placeholder for extra statements to
 * TODO: Execute (both during move and during undo, which makes this complex).
 * TODO: Think of stalactites and ability to fix the orientation during game play.
 * @author heineman
 */
public abstract class ActualMove implements Move {

   /** Assume always a source. */
   final Container srcContainer;

   /** Each move has a unique name, declared by invoker. */
   final String name;

   /** Optionally there may be a target. */
   final Container targetContainer;

   /** There may be a valid constraint at the source of a move. */
   Constraint sourceConstraint;

   /** There may be a valid constraint at the target of a move. */
   Constraint targetConstraint;


   /**
    * When a Move only has a source, then its given constraint is the source Constraint. The
    * targetConstraint is set to Truth() and the targetContainer becomes Optional.empty
    *
    * @param name       The designated (unique) name for a move.
    * @param src        The source container of the move.
    * @param srcCons    The source constraint associated with the move.
    */
   public ActualMove(String name, Container src, Constraint srcCons) {
      this.name = name;
      this.srcContainer = src;
      this.sourceConstraint = srcCons;

      this.targetContainer = null;
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
   public ActualMove(String name, Container src, Constraint srcCons, Container target, Constraint tgtCons) {
      this.name = name;
      this.srcContainer = src;
      this.sourceConstraint = srcCons;

      this.targetContainer = target;
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
      if (targetContainer != null) {
         return new AndConstraint(sourceConstraint, targetConstraint);
      }

      return sourceConstraint;
   }

   public Constraint getSourceConstraint() { return sourceConstraint; }
   public Constraint getTargetConstraint () { return targetConstraint; }

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
   public final Element getTarget() {
      if (targetContainer == null) { return null; }

      Iterator<Element> it = targetContainer.iterator();
      if (!it.hasNext()) { return null; }
      return it.next();
   }

   /** Get element being moved. */
   public abstract Element getMovableElement();

   /** Determine if single card being moved at a time. */
   public abstract boolean isSingleCardMove();

   /** Determine if single destination, or whether moved to all elements in the destination. */
   public abstract boolean isSingleDestination();

   public Container getSourceContainer() { return srcContainer; }
   public Optional<Container> getTargetContainer() {
      if (targetContainer == null) {
         return Optional.empty();
      }
      return Optional.of(targetContainer);
   }

   /**
    * Deny move from being feasible.
    */
   public void prevent() {
      this.sourceConstraint = new Falsehood();
      this.targetConstraint = new Falsehood();
   }


//
//   /**
//    * Two ActualMove objects are the same if they have the same name.
//    *
//    * @param o  potential comparator object.
//    * @return true if the Move objects have the same name.
//    */
//   @Override
//   public boolean equals(Object o) {
//      if (o == null) { return false; }
//
//      if (o instanceof Move) {
//         Move other = (Move) o;
//         return other.getName().equals(getName());
//      }
//
//      return super.equals(o);
//   }
//
//   @Override
//   public int hashCode() {
//      return getName().hashCode();
//   }
}
