package domain.constraints;

import domain.*;

/**
 * Implements BDD-like logic for rules 
 * DefaultFalseConstraint -- Returns False
 * DefaultTrueConstraint -- Returns True
 */
public class IfConstraint extends Constraint {
    public final Constraint trueBranch;
    public final Constraint falseBranch;
    public final Constraint constraint;

    public IfConstraint(Constraint c) {
        this.constraint = c;
        this.trueBranch = new Truth();
        this.falseBranch = new Falsehood();
    }

    public IfConstraint(Constraint c, Constraint trueBranch, Constraint falseBranch) {
        this.constraint = c;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }
}