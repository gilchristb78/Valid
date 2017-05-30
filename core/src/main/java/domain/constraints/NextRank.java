package domain.constraints;

import domain.*;

public class NextRank extends ConstraintExpr {

    final String element1; 
    final String element2;

    public NextRank (String e1, String e2) {
	super();
	this.element1 = e1;
        this.element2 = e2;
    }

    public String getElement1() { return element1; }
    public String getElement2() { return element2; }

}
