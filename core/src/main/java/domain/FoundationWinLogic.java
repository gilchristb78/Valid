package domain;

/**
 * Still wrestling with the extent to which the Domain Model of solitaire actually can 
 * be used to encode logic. For example, it is possible to instantiate actual objects
 * with these domain model classes to represent individual board states in any particular
 * solitaire variation.
 * 
 * This class has logic that determines a game has won when the number of cards in the 
 * foundation elements is equal to the number of cards in the initial decks.
 * 
 * @author heineman
 */
public class FoundationWinLogic extends Logic {

	/** Given a solitaire variation, has this game been won? */
	public boolean hasWon(Solitaire sol) {
		// Win if the number of cards in foundation elements 
		// is equal to the number of decks*52
		Stock stock = sol.getStock();
		int total = stock.getNumCards();

		Foundation found = sol.getFoundation();
		for (Element e : found) {
			total -= e.getSize();
		}

		return total == 0;
	}
}
