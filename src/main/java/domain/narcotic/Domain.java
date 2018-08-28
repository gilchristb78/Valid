package domain.narcotic;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.moves.*;
import domain.ui.Layout;
import domain.ui.layouts.StockTableauLayout;
import domain.ui.layouts.StockTableauPilesLayout;
import domain.win.BoardState;


/**
 * Programmatically construct full domain model for Narcotic.
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
            for (int i = 0; i < 4; i++) { tableau.add (new Pile()); }
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
            layout = new StockTableauPilesLayout();
        }

        return layout;
    }

	public Domain() {
        super("Narcotic");
        init();
    }

    private void init() {
		StockTableauLayout lay = new StockTableauLayout();

        placeContainer(getTableau());
        placeContainer(getStock());

		// Tableau to Tableau. Can move a card to the left if it is
		// going to a non-empty pile whose top card is the same rank
		// as moving card, and which is to the left of the source.
		//ToLeftOf toLeftOf = new ToLeftOf(MoveComponents.Destination, MoveComponents.Source);

		SameRank sameRank = new SameRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination));

		AndConstraint tt_move = new AndConstraint(new NotConstraint(new IsEmpty(MoveComponents.Destination)),
				sameRank);

		SingleCardMove tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, tt_move);
		addDragMove(tableauToTableau);

		// All top cards are the same.
		AllSameRank allSameRank = new AllSameRank(SolitaireContainerTypes.Tableau);

		RemoveMultipleCardsMove tableauRemove = new RemoveMultipleCardsMove("RemoveAllCards", tableau, allSameRank);
		addPressMove(tableauRemove);

		// note: for Python implementation, still need to implement concept of DeckDealMove, as well
        // as ResetDeckMove.

		// deal four cards from Stock
		NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
		DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, tableau);
		addPressMove(deckDeal);

		// reset deck by pulling together all cards from the piles.
		ResetDeckMove deckReset = new ResetDeckMove("ResetDeck", stock, new IsEmpty(MoveComponents.Source), tableau);
		addPressMove(deckReset);

		// wins once all cards are removed from tableau
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Tableau, 0);
		setLogic (state);
	}
}
