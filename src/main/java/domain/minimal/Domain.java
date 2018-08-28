package domain.minimal;

import domain.*;
import domain.Container;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.Deal;
import domain.deal.steps.DealToTableau;
import domain.moves.SingleCardMove;
import domain.ui.HorizontalPlacement;
import domain.ui.Layout;
import domain.ui.View;
import domain.win.BoardState;
import domain.freeCell.HomePile;
import java.awt.*;

/**
 * A minimal solitaire variation.
 *
 * There is a pile of cards all face up. There are four foundation piles. Deal any card to an
 * empty pile, however if pile already has card, then it must be same suit. Done when foundation
 * is full.
 */
public class Domain extends Solitaire {

    private Deal deal;
    private Layout layout;
    private Tableau tableau;
    private Foundation foundation;
    private Stock stock;

    /** A single pile */
    protected Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            tableau.add(new Pile());
        }
        return tableau;
    }

    /**
     * Default Foundation has four Home piles..
     *
     * @return
     */
    protected Foundation getFoundation() {
        if (foundation == null) {
            foundation = new Foundation();
            for (int i = 0; i < 4; i++) { foundation.add (new HomePile()); }
        }
        return foundation;
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
                    .append(new DealToTableau(52));
        }

        return deal;
    }

    /** Override layout as needed. */
    @Override
    public Layout getLayout() {
        if (layout == null) {
            layout = new Layout()
                    .add(SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point(15, 20),
                            card_width, card_height, card_gap))
                    .add(SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(100, 20),
                            card_width, card_height, card_gap));
        }

        return layout;
    }

	public Domain() {
        super("Minimal");
        init();
    }

    private void init() {
        registerElementAndView(new HomePile(), new View("HomePileView", "PileView", "HomePile"));

        placeContainer(getTableau());
        placeContainer(getFoundation());
        placeContainer(getStock());

		// Can move card to Tableau if empty or same suit
//        IfConstraint check = new IfConstraint(new IsEmpty(MoveComponents.Destination),
//            new Truth(),
//            new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)));

        // for SPLC paper, an even simpler move:
		addDragMove(new SingleCardMove("MoveCard", getTableau(), getFoundation(),
                new AndConstraint(
                        new IsEmpty(MoveComponents.Destination),
                        new IsAce(MoveComponents.MovingCard))));

		// wins once all cards are removed from tableau
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Tableau, 0);
		setLogic (state);
	}
}
