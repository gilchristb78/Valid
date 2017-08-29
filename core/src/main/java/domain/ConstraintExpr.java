package domain;

import java.util.*;

import domain.constraints.*;

/**
 * Records logic for allowed.
 *
 *    "Descending in order and alternating colors"
 *    "A single card"
 */
public abstract class ConstraintExpr { 

	public ConstraintExpr() { 
	}

	public String toString() {
		return this.getClass().getName();
	}

}
