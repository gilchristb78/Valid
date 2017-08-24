package domain;

/**
 * Note that the Stock represents the number of decks in the game.
 * 
 * Initially, the number of cards is set to 52 and during play the cards are dealt from the stock, 
 * so this value will change *if* the object actually represents the state of the game in mid-play.
 * 
 * @author heineman
 */
public class Stock extends Container {
	public final int numDecks;

	/** Default stock has single deck. */
	public Stock () {
		numDecks = 1;
		add(new Deck());
	}

	/** Create stock from any number of decks. */
	public Stock (int numDecks) {
		this.numDecks = numDecks; 
		for (int i = 0; i < numDecks; i++) {
		  add(new Deck());
		}
	}

	public int getNumDecks () {
		return numDecks;
	}
	
	public int getNumCards () { 
		return numDecks * 52;
	}
}
