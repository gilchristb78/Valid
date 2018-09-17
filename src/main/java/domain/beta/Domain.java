package domain.beta;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.moves.DeckDealMove;
import domain.moves.ResetDeckMove;
import domain.moves.SingleCardMove;
import domain.ui.*;
/*
import domain.ui.Layout;
import domain.ui.VerticalPlacement;
import domain.ui.HorizontalPlacement;
*/
import domain.win.BoardState;

import java.awt.*;

/**
 * Programmatically construct full domain model for "Hello-World"  Beta variation
 */
public class Domain extends Solitaire {

	private Deal deal;
	private Layout layout;
	private Foundation foundation;
    private Tableau tableau;
    private Stock stock;

    public Foundation getFoundation() {
    	if (foundation == null ) {
    		foundation = new Foundation();
    		for (int i = 0; i < 8; i++) {
    			foundation.add(new Pile());
			}
		}

		return foundation;
	}

    public Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 10; i++) { tableau.add (new Column()); }
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
					.add(SolitaireContainerTypes.Stock, new HorizontalPlacement(new Point(10, 10),
							card_width, card_height, card_gap))
					.add(SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point(50, 150),
							card_width, card_height*10, card_gap))
					.add(SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(210, 10),
							card_width, card_height, card_gap));
		}
		return layout;
	}

	public Stock getStock() {
	    if (stock == null) {
            // Beta has two decks
            stock = new Stock(2);
        }
        return stock;
    }

	public Domain() {
		super ("Beta");
		init();
	}

	private void init() {
		// we intend to be solvable
		setSolvable(true);

		placeContainer(getFoundation());
        placeContainer(getTableau());
        placeContainer(getStock());

        // add some test moves, no conditions yet
		/*
		addDragMove(
				new SingleCardMove("TableauToFoundation",
						tableau, foundation, new Truth()
				)
		);*/
		/*
		addDragMove(
				new SingleCardMove("TableauToTableau",
						tableau, tableau, new Truth()
				)
		);*/

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
