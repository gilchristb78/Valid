package domain.moves;

import domain.*;
import java.util.*;

/**
 * A column of cards are allowed to be moved
 */
public class ColumnMove extends Move {


	/** 
	 * Determine conditions for moving column of cards from src to target. 
	 */
	public ColumnMove (Container src, Container target, ConstraintStmt constraint) {
           super(src, target, constraint);
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

   /** Get element being moved. Hack to make work for FreeCell. */
   public Element   getMovableElement() {
     return new Column();
   }

}
