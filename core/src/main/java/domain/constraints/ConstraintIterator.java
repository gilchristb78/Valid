package domain.constraints;

import java.util.*;
import domain.Constraint;

public class ConstraintIterator implements Iterator<Constraint> {
    Constraint next;
    
    public ConstraintIterator (Constraint c) {
	next = c;
    }
    
    public boolean hasNext() {
	return next != null;
    }
    
    public Constraint next() { 
	Constraint retVal = next;
	next = next.getNext();
	return retVal;
    }
    
    public void remove() { 
	throw new RuntimeException ("not supported!");
    }
}
