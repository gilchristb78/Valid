@(RootPackage: NameExpr)
package @{Java(RootPackage)}.model;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Deal to all stacks
 */
public class DealStacksMove extends ks.common.model.Move {

	/** The deck. */
    protected Deck deck;

	/** Stacks. */
	protected Stack[] stacks;

	/**
	 * Deal cards (in order) to the given stacks.
	 */
	public DealStacksMove(Deck from, Stack[] stacks) {
		super();

		this.deck   = from;
		this.stacks = stacks;
	}

	/**
	 * Deal card to the given single stack.
	 */
	public DealStacksMove(Deck from, Stack singleStack) {
		super();

		this.deck   = from;
		this.stacks = new Stack[] { singleStack};
	}


	/**
	 * To undo this move, we move the cards from top of the piles back to the deck
	 */
	public boolean undo(ks.common.games.Solitaire game) {

		// move back
		for (int i = stacks.length -1; i >=0; i--) {
			deck.add(stacks[i].get());
			game.updateNumberCardsLeft(1);
		}
		return true;
	}

	/**
	 * Execute the move
	 * @@see ks.common.model.Move#doMove(ks.games.Solitaire)
	 */
	public boolean doMove(Solitaire game) {
		if (!valid (game)) {
			return false;
		}

		// EXECUTE
		for (int i = 0; i < stacks.length; i++) {
			stacks[i].add(deck.get());
			game.updateNumberCardsLeft(-1);
		}
		return true;
	}

	/**
	 * Validate the move.
	 * @@see ks.common.model.Move#valid(ks.games.Solitaire)
	 */
	public boolean valid(Solitaire game) {
		boolean validation = false;

		if (!deck.empty()) {
            validation = true;
        }

		return validation;
	}
}