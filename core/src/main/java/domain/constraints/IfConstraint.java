package domain.constraints;

import java.util.*;
import domain.*;

/**
 * Implements BDD-like logic for rules 
 * DefaultFalseConstraint -- Returns False
 * DefaultTrueConstraint -- Returns True
 */
public class IfConstraint extends ConstraintStmt {
    ConstraintStmt trueBranch;
    ConstraintStmt falseBranch;

    public IfConstraint (ConstraintExpr c) {
      super(c);
      this.trueBranch = new ReturnConstraint (new ReturnTrueExpression());
      this.falseBranch = new ReturnConstraint (new ReturnFalseExpression());
    }

    public IfConstraint (ConstraintExpr c, ConstraintStmt trueBranch,
                                           ConstraintStmt falseBranch) {
        super(c);
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public ConstraintStmt getTrueBranch() {
        return trueBranch;
    }

    public ConstraintStmt getFalseBranch() {
        return falseBranch;
    }
}
