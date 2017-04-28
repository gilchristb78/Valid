package domain;

import java.util.*;

import domain.constraints.*;

/**
 * Records logic for allowed.
 *
 *    "Descending in order and alternating colors"
 *    "A single card"
 */
public abstract class Constraint implements Iterable<Constraint> { 

	final Constraint next;

	public Constraint() { 
		next = null;
	}

	/** Compose new constraints from old constraints. */
	public Constraint(Constraint c) {
		next = c;
	}

	public Iterator<Constraint> iterator () {
		return new ConstraintIterator(this);
	}

	public Constraint getNext() { return next; }
	

	public String toString() {
		if (next == null) {
			return this.getClass().getName();
		}
		return this.getClass().getName() + " & " + next.toString();
	}

}