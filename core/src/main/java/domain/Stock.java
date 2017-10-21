package domain;

import domain.ui.PlacementGenerator;

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

    public Stock(PlacementGenerator places) {
        super(places);
        this.numDecks = 1;
        init();
    }

	/** Default stock has single deck. */
	public Stock () {
		super();
        this.numDecks = 1;
		init();
	}

    public Stock(PlacementGenerator places, int n) {
        super(places);
        this.numDecks = n;
        init();
    }

    /** Default stock has single deck. */
    public Stock (int n) {
        super();
        this.numDecks = n;
        init();
    }

	/** Create stock from any number of decks. */
	void init () {
		for (int i = 0; i < numDecks; i++) {
			add(new Deck());
		}
	}

}
