package domain.constraints;

import domain.*;

public class Descending extends Constraint {

    final String element;

    public Descending (String element) {
        super();
	this.element = element;
    }

    public String getElement() { return element; }

}
