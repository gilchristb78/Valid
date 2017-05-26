package domain.moves;

import domain.*;
import java.util.*;

/**
 * A column of cards are allowed to be moved
 */
public class ColumnMove extends Move {

	Container src;
	Container target;
	Constraint constraint;

	/** 
	 * Determine conditions for moving column of cards from src to target. 
	 */
	public ColumnMove (Container src, Container target, Constraint constraint) {
		this.src        = src;
		this.target     = target;
		this.constraint = constraint;
	}

	public String toString() {
		return src + " -> " + target + " : " + constraint;
	}

 /** Get the source element of this move type. */
   public Element   getSource() {
      Iterator<Element> it = src.iterator();
      if (it == null || !it.hasNext()) { return null; }
      return it.next();
   }

   /** Get the target element of this move type. */
   public Element   getTarget() {
      Iterator<Element> it = target.iterator();
      if (it == null || !it.hasNext()) { return null; }
      return it.next();
   }

   /** Get element being moved. Hack to make work for FreeCell. */
   public Element   getMovableElement() {
     return new Column();
   }

}
