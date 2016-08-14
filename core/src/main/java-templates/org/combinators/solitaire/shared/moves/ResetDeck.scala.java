@(RootPackage: NameExpr)
package @{Java(RootPackage)}.model;

import ks.common.games.Solitaire;
import ks.common.model.*;
/**
 * Represents the reset of the deck given an array of (or just a single) Stack objects
 * into which cards were dealt.
 */
public class ResetDeck extends ks.common.model.Move {
	/** The deck. */
    protected Deck deck;

	/** Stacks containing cards. */
	protected Stack[] stacks;

	/**
	 * Reset Deck by populating using cards from given stacks in order.
	 */
	public ResetDeck(Deck from, Stack[] stacks) {
		super();

		this.deck = from;
		this.stacks = stacks;
	}

	/**
	 * Reset Deck by populating using cards from the single stack.
	 */
	public ResetDeck(Deck from, Stack singleStack) {
		super();

		this.deck = from;
		this.stacks = new Stack[] { singleStack };
	}

	/**
	 * Each move should knows how to execute itself.
	 */
	public boolean doMove (Solitaire theGame) {

		// VALIDATE:
		if (valid (theGame) == false)
			return false;

		// EXECUTE:
		int numAdded = 0;
		for (int i = stacks.length-1; i >= 0; i--) {
			while (!stacks[i].empty()) {
				deck.add(stacks[i].get());
				numAdded++;
			}
		}

		// finally update the total number.
		theGame.updateNumberCardsLeft(numAdded);
		return true;
	}
	/**
	 * This move cannot be undone.
	 */
	public boolean undo(ks.common.games.Solitaire game) {
		return false;
	}

	/**
	 * Validate ResetDeck Move.
	 * @@param game edu.wpi.cs.soltaire.games.Solitaire
	 */
	public boolean valid (ks.common.games.Solitaire game) {
		// VALIDATION:
		boolean validation = false;

		if (deck.empty())
			validation = true;

		return validation;
	}
}