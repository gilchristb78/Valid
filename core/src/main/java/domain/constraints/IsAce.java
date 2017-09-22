package domain.constraints;

import domain.*;

/**
 * This constraint is (for some reason) defined with extra precision. Reason is that
 * determining the rank of a Stack uses .rank() method, while rank of a Card
 * uses .getRank() method.
 */
public class IsAce extends UnaryConstraintExpr {

    final Element type; 

    public IsAce (Element t, String element) {
	    super(element);
        this.type = t;
    }

    public Element getType() { return type; }
}
