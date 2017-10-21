package domain.constraints;

import domain.*;

/**
 * Arbitrary Boolean Expression 
 */
public class BooleanExpression extends Constraint {
    final String exp;


    public BooleanExpression (String exp) {
        super();
	this.exp = exp;
    }

    public String getExpression() {
        return exp;
    }

}
