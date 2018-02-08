package domain.freeCell;

import domain.Constraint;
import domain.Reserve;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.Deal;
import domain.deal.steps.DealToFoundation;
import domain.deal.steps.DealToTableau;


/**
 * FreeCell variation with 2 reserves, four foundations and a Tableau of eight columns of six
 * cards each. No card can ever be moved to the Tableau. Four random cards are dealt face up
 * to the foundation.
 * Cards are built up on the foundation piles in increasing rank, wrapping around until 13 cards
 * remain in each pile.
 *
 * Need way to restrict foundation piles to have no more than 13 cards.
 */
public class Stalactites extends FreeCellDomain {

	private Deal deal;
	private Reserve reserve;

    @Override
    public Constraint buildOnFoundation(MoveInformation bottom) {
        Full isFull = new Full(MoveComponents.Destination);
        return new AndConstraint(
                new NotConstraint(isFull),
                new NextRankWrapAround(bottom, new TopCardOf(MoveComponents.Destination)));
    }

    /**
     * Make two reserve piles
     *
     * @return
     */
    protected Reserve getReserve() {
        if (reserve == null) {
            reserve = new Reserve();
            for (int i = 0; i < 2; i++) { reserve.add (new FreePile()); }
        }
        return reserve;
    }

    /** Override deal as needed. */
    @Override
    public Deal getDeal() {
        if (deal == null) {
            deal = new Deal()
                    .append(new DealToFoundation())
                    .append(new DealToTableau(6));
        }

        return deal;
    }


	public Stalactites() {
		super ("Stalactites");

		// remove moves to the tableau.

        // To eliminate a move, simply change the source and target constraints to Falsehood
        prevent(getRules().drags(), "PlaceFreePileCard");
        prevent(getRules().drags(), "MoveColumn");
    }
}
