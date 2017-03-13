package domain;

/**

   Holds a number of distinct elements. Often is used to arrange
   elements in specific rigid format in layout.

   Will need to offer traversal/visiting behavior.

 */

import java.util.*;

public class Container implements Iterable<Element> {

    /** Use what is available. */
    ArrayList<Element> elements = new ArrayList<Element>();

    /** Return the size of the container. */
    public int getSize() {
	return elements.size();
    }

    /**
     * Attempt to add element to Container, returning true if 
     * successful; false if a duplicate.
     */
    public boolean add(Element e) {
	if (elements.contains(e)) {
	    return false;
	}

	elements.add(e);
	return true;
    } 

    /**
     * Attempt to remove element from Container, returning true 
     * if successful; false if not present.
     */
    public boolean remove(Element e) {
	if (!elements.contains(e)) {
	    return false;
	}

	elements.remove(e);
	return true;
    }

    /** 
     * iterator over all elements. 
     */
    public Iterator<Element> iterator() {
	return elements.iterator();
    }
}
