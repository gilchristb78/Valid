package domain.constraints;

import domain.*;

/**
 * Arbitrary Expression 
 */
public class ExpressionConstraint extends ConstraintExpr {
    final String lhs;
    final String rhs;
    final String op;


    public ExpressionConstraint (String lhs, String op, String rhs) {
        super();
        this.lhs = lhs;
	this.rhs = rhs;
	this.op = op;
    }

    public String getLHS() {
        return lhs;
    }

    public String getOp () { 
	return op;
    }

    public String getRHS() {
        return rhs;
    }
}
