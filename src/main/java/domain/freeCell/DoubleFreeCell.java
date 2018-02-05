package domain.freeCell;

import domain.*;
import domain.deal.Deal;
import domain.deal.DealStep;
import domain.deal.ElementTarget;
import domain.deal.Payload;
import domain.deal.steps.DealToFoundation;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.FilterAces;
import domain.deal.steps.FilterByRank;


/**
 * FreeCell with two decks, six reserves.
 */
public class DoubleFreeCell extends FreeCellDomain {

	private Deal deal;
	private Stock stock;
	private Reserve reserve;
	private Tableau tableau;

	/**
	 * Require two decks.
	 */
	@Override
	protected Stock getStock() {
		if (stock == null) { stock = new Stock(2); }
		return stock;
	}


    /**
     * Default Reserve has five Free piles..
     *
     * @return
     */
    protected Reserve getReserve() {
        if (reserve == null) {
            reserve = new Reserve();
            for (int i = 0; i < 6; i++) { reserve.add (new FreePile()); }
        }
        return reserve;
    }

    /** Override deal as needed. */
    @Override
    public Deal getDeal() {
        if (deal == null) {
            deal = new Deal()
                    .append(new FilterAces(4))
                    .append(new DealToFoundation())
                    .append(new DealToTableau(10));
        }

        return deal;
    }

    /**
     * Default Tableau has eight columns
     *
     * @return
     */
    protected Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 10; i++) { tableau.add (new Column()); }
        }
        return tableau;
    }

	public DoubleFreeCell() {
		super ("DoubleFreeCell");
	}
}
