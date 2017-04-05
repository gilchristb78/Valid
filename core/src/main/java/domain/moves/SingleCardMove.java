package domain.moves;

import domain.*;

/**
 * A single card being moved.
 *
 * These move classes might not be necessary.
 */
public class SingleCardMove extends Move {

	Container src;
	Container target;
	Constraint constraint;

	/** 
	 * Determine conditions for moving column of cards from src to target. 
	 */
	public SingleCardMove (Container src, Container target, Constraint constraint) {
		this.src        = src;
		this.target     = target;
		this.constraint = constraint;
	}


	public String toString() {
		return src + " -> " + target + " : " + constraint;
	}
	
}
