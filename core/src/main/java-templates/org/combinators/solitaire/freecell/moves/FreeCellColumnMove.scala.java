@(RootPackage: Name, NameOfTheGame: SimpleName)

package @Java(RootPackage);

import ks.common.games.Solitaire;
import ks.common.model.*;

import @Java(RootPackage).*;
//import Java(RootPackage).model.*;

public class FreeCellColumnMove extends Move {
	/** Number of cards in column being moved. */
	private int numInColumn;

	/** Column of cards being moved. */
	private Column movingColumn;

	/** Destination column. */
	private Column toCol;

	/** Source column. */
	private Column fromCol;

	/** Column being moved plus also number of cards. */
	public FreeCellColumnMove(Column fromCol, Column movingColumn, int numInColumn, Column toCol) {
		super();

		this.fromCol = fromCol;

		this.movingColumn = movingColumn;
		this.numInColumn = numInColumn;
		this.toCol = toCol;
	}

	/** Column being moved only. */
	public FreeCellColumnMove(Column fromCol, Column movingColumn, Column toCol) {
		super();

		this.fromCol = fromCol;

		this.movingColumn = movingColumn;
		this.numInColumn = movingColumn.count();
		this.toCol = toCol;
	}

	/**
	 * To undo this move, we move the cards from the toPile back to the fromPile
	 * __param__ theGame ks.games.Solitaire 
	 */
	public boolean undo(ks.common.games.Solitaire game) {
		// move back
		toCol.select (numInColumn);
		fromCol.push(toCol.getSelected());
		return true;
	}

	/** Execute the move
	 * __see__ ks.common.model.Move#doMove(ks.games.Solitaire)
	 */
	public boolean doMove(Solitaire game) {
		if (!valid(game)) {
			return false;
		}

		// EXECUTE!
		if (movingColumn == null) {
			fromCol.select(numInColumn);
			toCol.push (fromCol.getSelected());
		} else {
			toCol.push (movingColumn);
		}

		return true;
	}

	/**
	 * Validate the move.
	 * __see__ ks.common.model.Move#valid(ks.games.Solitaire)
	 */
	public boolean valid(Solitaire game) {
		Card topCard = toCol.peek();

		// verify that column is in order and alternating suits
		Column checkColumn = movingColumn;
		Column source;
		Card movingCard;
		int offset;
		if (checkColumn == null) {
			// we are a calculated move, so we must base decisions on fromCol
			offset = fromCol.count();
			source = fromCol; 
			movingCard = fromCol.peek (offset-numInColumn);
		} else {
			// moving column has everything
			offset = movingColumn.count();
			source = movingColumn;
			movingCard = movingColumn.peek(0);
		}

		if (!source.descending (offset-numInColumn, offset) ||
				!source.alternatingColors(offset-numInColumn, offset))
			return false;

		// validate that there are at least n-1 vacant spots for a 
		// column of size n being moved
		int numVacant = ((@Java(NameOfTheGame))game).numberVacant();

		// can't use the column into which we are moving as a count.
		if (toCol.empty()) { numVacant--; }
		if (numVacant < numInColumn - 1)
			return false;

		// moves to empty column are always allowed.
		if (toCol.empty())
			return true;

		if (topCard.getRank() == (movingCard.getRank() + 1)
				&& topCard.oppositeColor(movingCard)) {
			return true;
		}
		return false;
	}
}
