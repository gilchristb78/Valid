package domain.moves;

import domain.*;
import java.util.*;

/**
 * Deck is reconstituted from the target elements.
 */
public class ResetDeckMove extends Move {

    /** 
     * Determine conditions for resetting deck. 
     */
    public ResetDeckMove (String name, Container src, Container target, ConstraintStmt constraint) {
	super(name, src, target, constraint);
    }

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

   /**
    * Get element being moved. 
    * 
    * Even though no card is dragged, this is accurate.
    */
   public Element   getMovableElement() {
     return new Card(Rank.ACE, Suit.SPADES); 
   }
}
