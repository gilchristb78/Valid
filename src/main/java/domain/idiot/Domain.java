package domain.idiot;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.deal.Deal;
import domain.deal.steps.DealToTableau;
import domain.moves.*;
import domain.ui.Layout;
import domain.ui.layouts.StockTableauLayout;
import domain.win.BoardState;

/**
 * Programmatically construct full domain model for Idiot.
 */
public class Domain extends Solitaire {

	private Deal deal;
	private Layout layout;

    private Tableau tableau;
    private Stock stock;

    /**
     * Default Klondike Tableau has seven buildable piles.
     *
     * @return
     */
    protected Container getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 4; i++) { tableau.add (new Column()); }
        }
        return tableau;
    }

    /**
     * Default Klondike has a single Stock of a single deck of cards.
     *
     * @return
     */
    protected Container getStock() {
        if (stock == null) { stock = new Stock(); }
        return stock;
    }


    /** Override deal as needed. */
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal()
                    .append(new DealToTableau(1));
		}

		return deal;
	}

    /** Override layout as needed. */
    @Override
    public Layout getLayout() {
        if (layout == null) {
            layout = new StockTableauLayout();
        }

        return layout;
    }

	public Domain() {
        super("Idiot");
        init();
    }

    private void init() {
		// we intend to be solvable
		setSolvable(true);

        placeContainer(getTableau());
        placeContainer(getStock());

		// When only aces are left, there are 48 points.
        BoardState state = new BoardState();
        state.add(SolitaireContainerTypes.Stock, 0);
        state.add(SolitaireContainerTypes.Tableau, 4);
		setLogic (state);

		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

		// Tableau to Tableau
		SingleCardMove tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, isEmpty);
		addDragMove(tableauToTableau);

		// this special method is added by gameDomain to be accessible here.
		HigherRankSameSuit sameSuitHigherRankVisible = new HigherRankSameSuit(MoveComponents.Source);

		AndConstraint and = new AndConstraint(new NotConstraint(new IsEmpty(MoveComponents.Source)), sameSuitHigherRankVisible);

		RemoveSingleCardMove removeCardFromTableau = new RemoveSingleCardMove("RemoveCard", tableau, and);
		addClickMove(removeCardFromTableau);

		// Remove a card from the tableau? This can be optimized by a click
		// do I allow another Rule? Or reuse existing one?
		// Not sure how to deal with MOVE with a single PRESS
		// That is, this will not be the head part of a drag operation.

		// deal four cards from Stock
		NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
		DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, tableau);
		addPressMove(deckDeal);

	}
}
