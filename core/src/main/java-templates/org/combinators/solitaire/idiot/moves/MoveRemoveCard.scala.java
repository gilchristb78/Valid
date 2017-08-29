@(RootPackage: Name, NumColumns: Expression)
package @{Java(RootPackage)}.model;

import ks.common.games.*;
import ks.common.model.*;


/**
 * Represents the reset of the deck.
 */
public class MoveRemoveCards extends ks.common.model.Move {

	/** Columns */
	protected Column columns;
	protected Card removingCard = null;

	protected Column cols[];

	public MoveRemoveCards(Column columns, Card removed, Column col1, Column col2, Column col3, Column col4) {
		super ();

		this.columns = columns;
		this.removingCard = removed;

		cols = new Column[] { col1, col2, col3, col4 };
	}

	/**
	 * Each move should knows how to execute itself.
	 */
	public boolean doMove (Solitaire theGame) {

		// VALIDATE:
		if (valid (theGame) == false)
			return false;

		// EXECUTE: save card (for future undo) increment score by one
		removingCard = columns.get();

		// update count in score
		theGame.updateScore (+1);
		return true;
	}
	
	/**
	 * Undo move.
	 */
	public boolean undo(ks.common.games.Solitaire theGame) {
		// VALIDATE:
		if (removingCard == null) return false;

		// UNDO:
		// put the card back and reduce score by one
		columns.add (removingCard);

		// update count in score
		theGame.updateScore (-1);

		return true;
	}

	/**
	 * Validate Move.
	 * @@param game edu.wpi.cs.soltaire.games.Solitaire
	 */
	public boolean valid (ks.common.games.Solitaire theGame) {
		// VALIDATION:
		boolean validation = false;

		Column cols[] = { (Column) theGame.getModelElement ("Columns1"),
				(Column) theGame.getModelElement ("Columns2"),
				(Column) theGame.getModelElement ("Columns3"),
				(Column) theGame.getModelElement ("Columns4") };

		// Detected 3/28/2011
		// empty columns are not eligible.
		if (columns.empty()) { return false; }
		
		// Detected (9:54 PM Oct/21/2001).
		if (columns.rank() != Card.ACE) {
			for (int i = 0; i < 4; i++) {
				// skip 'from' column and empty ones
				if (cols[i] == columns || cols[i].empty()) continue;

				// must be same suit
				if (cols[i].suit() != columns.suit()) continue;

				// if the current column (has same suit) and has larger rank than the from column, we can remove.
				// Note ACES handles specially.
				if (cols[i].rank() > columns.rank() || cols[i].rank() == Card.ACE) {
					validation = true;
				}
			}
		}

		return validation;
	}
}