package domain.constraints;

import domain.*;

/**
 * If c1 is not valid, then c2 must be valid
 */
public class OrConstraint extends Constraint {
    Constraint c1;
    Constraint c2;

    public OrConstraint (Constraint c1, Constraint c2) {
	super();
	this.c1 = c1;
	this.c2 = c2;
    }
}
