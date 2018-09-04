package domain.alpha;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.moves.DeckDealMove;
import domain.moves.ResetDeckMove;
import domain.moves.SingleCardMove;
import domain.ui.Layout;
import domain.ui.VerticalPlacement;
import domain.win.BoardState;

import java.awt.*;

/**
 * Programmatically construct full domain model for "Hello-World"  Alpha variation
 */
public class Domain extends Solitaire {

	private Deal deal;
	private Layout layout;
    private Tableau tableau;
    private Stock stock;

    public Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            tableau.add(new Pile());
        }
        return tableau;
    }

	/** Override deal as needed. Nothing dealt. */
	@Override
	public Deal getDeal() {
        if (deal == null) {
			deal = new Deal();
		}
		return deal;
	}

    /** Override layout as needed. */
	@Override
	public Layout getLayout() {
		if (layout == null) {
			layout = new Layout()
					.add(SolitaireContainerTypes.Stock, new VerticalPlacement(new Point(100, 10),
							card_width, card_height, card_gap))
					.add(SolitaireContainerTypes.Tableau, new VerticalPlacement(new Point(200, 10),
							card_width, card_height, card_gap));
		}
		return layout;
	}

	public Stock getStock() {
	    if (stock == null) {
            // Alpha has a single deck
            stock = new Stock(1);
        }
        return stock;
    }

	public Domain() {
		super ("Alpha");
		init();
	}

	private void init() {
		// we intend to be solvable
		setSolvable(true);

        placeContainer(getTableau());
        placeContainer(getStock());

        // deal card from stock
        NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, tableau);
        addPressMove(deckDeal);

        // When all cards are in the AcesUp and KingsDown
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Tableau, 52);
		setLogic (state);
	}
}
