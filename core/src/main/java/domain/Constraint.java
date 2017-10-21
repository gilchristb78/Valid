package domain;

import java.util.*;

import domain.code.CodeGenFacility;
import domain.constraints.*;

/**
 * Records logic for allowed.
 *
 *    "Descending in order and alternating colors"
 *    "A single card"
 */
public abstract class Constraint {

	public Constraint() {
	}

	/** Debugging purposes only. */
	public String toString() {
		return this.getClass().getName();
	}

	/**
	 * There needs to be clean separation between the constraint and the code which will ultimately be synthesized.
	 * <p>
	 *
	 *
	 */
	public CodeGenFacility addCodeGen(CodeGenFacility c) {
		return c;
	}
}
