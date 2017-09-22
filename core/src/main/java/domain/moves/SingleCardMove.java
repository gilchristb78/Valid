package domain.moves;

import domain.*;
import java.util.*;

/**
 * A single card being moved.
 *
 * This has been subverted just for FreeCell (for now)...
 *
 * These move classes might not be necessary.
 */
public class SingleCardMove extends Move {

    /** 
     * Determine conditions for moving column of cards from src to target. 
     */
    public SingleCardMove (String name, Container src, Container target, ConstraintStmt constraint) {
	super(name, src, target, constraint);
    }

    public SingleCardMove (String name, Container src, ConstraintStmt constraint) {
       super(name,src, constraint); 
    }

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

   /** Get element being moved. Hack to make work for FreeCell. */
   public Element   getMovableElement() {
     return new Card(Rank.ACE, Suit.SPADES); 
   }
}
