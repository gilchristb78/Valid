package domain.freeCell;

import domain.Constraint;
import domain.constraints.IsKing;
import domain.constraints.MoveInformation;


/**
 * Programmatically construct full domain model for FreeCell.
 */
public class SuperChallengeFreeCell extends ChallengeFreeCell {

	/** Even worse: Only Kings allowed in free Tableau spaces. */
	@Override
	public Constraint buildOnEmptyTableau(MoveInformation bottom) {
		return new IsKing(bottom);
	}


	public SuperChallengeFreeCell() {
		super ("SuperChallengeFreeCell");
	}
}
