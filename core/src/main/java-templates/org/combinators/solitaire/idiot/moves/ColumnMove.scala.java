@(RootPackage: NameExpr, ColumnToColumnCondition: Seq[Statement])
package @{Java(RootPackage)}.moves;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Move card between columns
 */
public class ColumnColumnMove extends ks.common.model.Move {

	private Card theCard;
	private Column toCol;
	private Column fromCol;

    /**
     * Moves card from one pile to another.
     */
    public ColumnColumnMove(Column fromCol, Card theCard, Column toCol) {
        super();

        this.fromCol = fromCol;
		this.theCard = theCard;
		this.toCol = toCol;
    }

    /**
     * To undo this move, we move the cards from top of the piles back to the deck
     */
    public boolean undo(ks.common.games.Solitaire game) {
		// move back
		fromCol.add(toCol.get());
		return true;
	}

    /**
     * Execute the move
     * @@see ks.common.model.Move#doMove(ks.games.Solitaire)
     */
    public boolean doMove(Solitaire game) {
		if (!valid(game)) {
			return false;
		}
		// EXECUTE!
		toCol.add (theCard);
		return true;
	}

    /**
     * Validate the move.
     * @@see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {
		if (toCol.empty())
			return true;
		
		@Java(ColumnToColumnCondition)
		
		return false;
	}
}