package domain.spider;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.moves.ColumnMove;
import domain.moves.DeckDealMove;
import domain.moves.*;
import domain.ui.*;
/*
import domain.ui.Layout;
import domain.ui.VerticalPlacement;
import domain.ui.HorizontalPlacement;
*/
import domain.win.BoardState;

import java.awt.*;

/**
 * Spiderette is simply Spider with a single deck and a Klondike deal/tableau
 */
public class Spiderette extends SpiderDomain{

	private Deal deal;
	private Stock stock;

	/** Spiderette uses a Klondike-style deal. */
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal();

			// each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
			// don't forget zero-based indexing.
			for (int pileNum = 1; pileNum < 7; pileNum++) {
				deal.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, pileNum), new Payload(pileNum, false)));
			}

			// finally each one gets a single faceup Card
			deal.append(new DealToTableau());
		}

		return deal;
	}

	/** Spiderette uses only a single deck */
	@Override
	public Stock getStock() {
		if (stock == null) {
			stock = new Stock(1);
		}
		return stock;
	}

	/** With a klondike deal, we need a klondike tableau */
	@Override
	public Tableau getTableau() {
		if (tableau == null) {
			tableau = new Tableau();
			for (int i = 0; i < 7; i++) { tableau.add (new BuildablePile()); }
		}
		return tableau;
	}

	public Spiderette(){ super("Spiderette"); }

}
