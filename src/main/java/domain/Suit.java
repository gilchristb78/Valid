package domain;

/** declared in this fashion so sameColor is simply check modulo 2. */
@Deprecated
enum Suit {
    CLUBS(0),
    DIAMONDS(1),
    SPADES(2),
    HEARTS(3);

    final int suit;

    Suit(int suit) {
	this.suit = suit;
    }

    public int getSuit() {
	return suit;
    }

    public boolean sameColor(Suit s) {
	return (s.suit % 2) == (suit % 2);
    }

    public boolean oppositeColor(Suit s) {
	return (s.suit % 2) != (suit % 2);
    }

}
