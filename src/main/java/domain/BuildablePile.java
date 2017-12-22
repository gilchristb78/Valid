package domain;

public class BuildablePile extends Element {
	int count; 

	/** Card is a single size. */
	public int getSize() {
		return count;
	}

	public boolean viewOneAtATime() { return false; }
}
