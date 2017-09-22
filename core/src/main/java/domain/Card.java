package domain;

public class Card extends Element {
	final Rank rank;
	final Suit suit;

	public Card (Rank rank, Suit suit) {
           this.rank = rank;
           this.suit = suit;
	}

        public Card () { 
	  this (Rank.ACE, Suit.SPADES);
	}

	/** Card is a single size. */
	public int getSize() {
		return 1;
	}
}
