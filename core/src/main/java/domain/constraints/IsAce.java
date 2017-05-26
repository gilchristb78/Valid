package domain.constraints;

import domain.*;

public class IsAce extends Constraint {

    final Element type; 
    final String element;

    public IsAce (Element t, String element) {
	super();
        this.type = t;
	this.element = element;
    }

    public Element getType() { return type; }
    public String getElement() { return element; }
}
