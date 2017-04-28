package domain;

public class Card extends Element {
	public Card (Rank rank, Suit suit) {
	}

	/** Card is a single size. */
	public int getSize() {
		return 1;
	}
}
