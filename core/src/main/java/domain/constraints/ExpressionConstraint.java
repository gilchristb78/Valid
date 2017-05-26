package domain.constraints;

import domain.*;

public class ExpressionConstraint extends Constraint {

    final String lhs;
    final String op;
    final String rhs;

    public ExpressionConstraint (String lhs, String op, String rhs) {
        super();
	this.lhs = lhs;
	this.op = op;
	this.rhs = rhs;
    }

    public String getLHS() { return lhs; }
    public String getOp() { return op; }
    public String getRHS() { return rhs; }

}
