package domain;

public class Card extends Element {

	public enum Suits {
		Clubs(1), Diamonds(2), Hearts(3), Spades(4);

		int val;
		Suits(int val) { this.val = val; }
		public int value() { return val; }
	}

	public enum Ranks {
		Ace(1), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9), Ten(10), Jack(11), Queen(12), King(13);

		int val;
		Ranks(int val) { this.val = val; }
		public int value() { return val; }
	}

	public Card () { }

	public boolean viewOneAtATime() { return true; }
}
