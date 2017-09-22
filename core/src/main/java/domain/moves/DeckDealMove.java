package domain.moves;

import domain.*;
import java.util.*;

/**
 * A number of cards are dealt from the Stock.
 *
 */
public class DeckDealMove extends Move {

    /** 
     * Determine conditions for moving column of cards from src to target. 
     */
    public DeckDealMove (String name, Container src, Container target, ConstraintStmt constraint) {
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
