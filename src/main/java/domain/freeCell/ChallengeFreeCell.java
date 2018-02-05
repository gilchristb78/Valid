package domain.freeCell;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.Deal;
import domain.deal.DealStep;
import domain.deal.ElementTarget;
import domain.deal.Payload;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.FilterAces;
import domain.deal.steps.FilterByRank;
import domain.moves.ColumnMove;
import domain.moves.SingleCardMove;
import domain.ui.Layout;
import domain.ui.ReserveFoundationTableauLayout;
import domain.ui.View;
import domain.win.BoardState;


/**
 * Programmatically construct full domain model for FreeCell.
 */
public class ChallengeFreeCell extends FreeCellDomain {

	private Deal deal;

	/** Only here for pass-through to extensions. */
	protected ChallengeFreeCell(String name) { super(name); }

	/** Override deal as needed. Move Aces and Deuces to top*/
	@Override
	public Deal getDeal() {
		if (deal == null) {
            deal = new Deal()
                    .append(new FilterAces())
                    .append(new FilterByRank(Card.Ranks.Two))
                    .append(super.getDeal());
        }

		return deal;
	}


	public ChallengeFreeCell() {
		super ("ChallengeFreeCell");
	}
}
