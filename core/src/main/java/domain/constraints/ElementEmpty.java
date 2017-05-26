package domain.constraints;

import domain.*;

public class ElementEmpty extends Constraint {

    final String element;

    public ElementEmpty (String element) {
        super();
	this.element = element;
    }

    public String getElement() { return element; }

}
