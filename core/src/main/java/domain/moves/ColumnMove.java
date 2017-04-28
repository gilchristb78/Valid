package domain.moves;

import domain.*;

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
}
