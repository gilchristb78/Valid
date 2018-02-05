package domain.freeCell;

import domain.Constraint;
import domain.constraints.IsKing;
import domain.constraints.MoveInformation;
import domain.deal.Deal;
import domain.deal.steps.DealToReserve;
import domain.deal.steps.DealToTableau;


/**
 * ForeCell has four cards pre-dealt to reserve, all others to the tableau.
 * Empty Tableaus can only have kings.
 */
public class ForeCell extends FreeCellDomain {

	private Deal deal;

	/** Only Kings allowed in free Tableau spaces. */
	@Override
	public Constraint buildOnEmptyTableau(MoveInformation bottom) {
		return new IsKing(bottom);
	}

	/** Override deal as needed. Move Aces and Deuces to top*/
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal()
					.append(new DealToTableau(6))
					.append(new DealToReserve( ));
		}

		return deal;
	}


	public ForeCell() {
		super ("ForeCell");
	}
}
