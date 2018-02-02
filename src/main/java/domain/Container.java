package domain;

import java.util.*;

/**
 * Holds a number of distinct elements. Often is used to arrange
 * elements in specific rigid format in layout.
 *
 * Will need to offer traversal/visiting behavior.
 *
 * While initial concept was for all types within a container to be the same, there is the ability
 * to iterate over all unique types within a container, so theoretically one can place multiple
 * typed objects in the same container.
 */
public class Container implements Iterable<Element> {

	/** Use what is available. */
	ArrayList<Element> elements = new ArrayList<>();

	/** Associated ContainerType for this Container. */
	public final ContainerType type;

	/**
	 * Default constructor for subclasses (i.e., Stock) which may be present in the model
	 * but are not visible.
	 *
	 * Use this only for non-visible Containers (again, only likely for Stock)
	 */
	public Container(ContainerType type) {
		this.type = type;
	}


	/** Return the size of the container. */
	public int size() {
		return elements.size();
	}

	/** Same type. */
	public boolean isSame(Container c) {
		if (c == null) { return false; }

		return (this.getClass() == c.getClass());
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
		Set<String> set = new HashSet<>();
		for (Element e : elements) {
			String name = e.getClass().getSimpleName();
			set.add(name);
		}
		return set.iterator();
	}

	/**
	 * Return iterator over all elements.
	 */
	@Override
	public Iterator<Element> iterator() {
        return elements.iterator();
	}
}
