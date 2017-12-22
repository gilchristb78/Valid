package domain;

public class Pile extends Element {
	int count; 

	/** Card is a single size. */
	public int getSize() {
		return count;
	}

	public boolean viewOneAtATime() { return true; }
}
