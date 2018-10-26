package domain.napoleon;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.Deal;
import domain.deal.steps.DealToFoundation;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.FilterAces;
import domain.moves.DeckDealMove;
import domain.moves.DeckDealNCardsMove;
import domain.moves.RowMove;
import domain.moves.SingleCardMove;
import domain.ui.*;
import domain.win.BoardState;

import java.awt.*;


/**
 * DOmain for Napoleon
 */
public class Domain extends Solitaire {

	private Deal deal;
	private Layout layout;
	private Foundation foundation;
	private Tableau tableau;
	private Waste waste;
	private Stock stock;

	/** Override deal as needed. */
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal()
                    .append(new DealToTableau(10));
		}

		return deal;
	}

	/** Override layout as needed. */
	@Override
	public Layout getLayout() {
		if (layout == null) {
                layout = new Layout()
                    .add(SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(200, 10),
                            card_width, card_height, card_gap))
                    .add(SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point (120, 120),
                            card_width, card_height*13, card_gap))
                    .add(SolitaireContainerTypes.Stock, new VerticalPlacement(new Point (10, 10),
                            card_width, card_height, card_gap))
                    .add(SolitaireContainerTypes.Waste, new VerticalPlacement(new Point (10, 200),
                            card_width, card_height, card_gap));

		}

		return layout;
	}

    /**
     * Two decks for this game.
     */
    protected Stock getStock() {
        if (stock == null) { stock = new Stock(2); }
        return stock;
    }


    /**
     * Foundation contains 8 piles.
     */
    protected Foundation getFoundation() {
        if (foundation == null) {
            foundation = new Foundation();
            for (int i = 0; i < 8; i++) { foundation.add (new Pile()); }
        }
        return foundation;
    }

    /**
     * Default Tableau has ten columns, each with four cards
     */
    protected Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 10; i++) { tableau.add (new Column()); }
        }
        return tableau;
    }

    /**
     * Default Waste pile
     */
    protected Waste getWaste() {
        if (waste == null) {
            waste = new Waste();
            waste.add (new WastePile());
        }
        return waste;
    }

	public Domain() {
		super ("Napoleon");
		init();
    }

    private void init() {

        placeContainer(getFoundation());
        placeContainer(getTableau());
        placeContainer(getWaste());
        placeContainer(getStock());

        // eventually, this will be moved to domain superclasses
        registerElementAndView(new WastePile(), new View("WastePileView", "PileView", "WastePile"));

        // Deal Deck to WastePile
        // already take advantage of the parameter for this domain.
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

        // Deal card from deck
        Constraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        addPressMove(new DeckDealMove("DealDeck", getStock(), deck_move, getWaste()));

        // WastePile to Tableau
        //addDragMove(new SingleCardMove("MoveCard", getWaste(), getTableau(), wastePileToTableauMove));

        // WastePile to Foundation
        Constraint wasteToFoundation = new IfConstraint(isEmpty,
                new IsAce(MoveComponents.MovingCard),
                new AndConstraint (
                    new NextRank(MoveComponents.MovingCard, topDestination),
                    new SameSuit(MoveComponents.MovingCard, topDestination)));

        addDragMove(new SingleCardMove("MoveCard", getWaste(), getFoundation(), wasteToFoundation));

        // Tableau to Tableau
       //addDragMove(new SingleCardMove("MoveCard", getTableau(), getTableau(), wastePileToTableauMove));

        // Tableau to Foundation
        //addDragMove(new SingleCardMove("MoveCard", getTableau(), getFoundation(), wastePileToTableauMove));


		// When foundation is full, we are done.
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Foundation, 104);
		setLogic (state);
	}
}
