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
	public int size() {
		return elements.size();
	}

  	/** Same type. */
	public boolean isSame(Container c) {
	  if (c == null) { return false; }
	  if (this.getClass() == c.getClass()) { 
             return true; 
          }
	  return false;
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
         * Return iterator of unique types. Likely easier way
         * of doing this in Scala... Also, outputs SimpleName for simplicity
         */
	public Iterator<String> types() {
	   ArrayList<String> elems = new ArrayList<String>();
	   for (Element e : elements) {
		String name = e.getClass().getSimpleName();
		if (!elems.contains(name)) {
		   elems.add(name);
		}
           }
	   return elems.iterator();
	}

	/** 
	 * iterator over all elements. 
	 */
	public Iterator<Element> iterator() {
		return elements.iterator();
	}
}
