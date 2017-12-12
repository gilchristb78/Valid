package domain;

public class Deck extends Element {
	int count; 

	/** Card is a single size. */
	public int getSize() {
		return count;
	}

	public boolean viewOneAtATime() { return true; }
}
