package domain.castle;

import domain.*;
import domain.Container;
import domain.constraints.*;
import domain.constraints.movetypes.*;
import domain.deal.*;
import domain.deal.steps.DealToFoundation;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.FilterAces;
import domain.moves.RowMove;
import domain.ui.CalculatedPlacement;
import domain.ui.Layout;
import domain.ui.VerticalPlacement;
import domain.ui.PlacementGenerator;
import domain.win.BoardState;

import java.awt.*;
import java.util.Iterator;


/**
 * Programmatically construct full domain model for Castle.
 */
public class Domain extends Solitaire {

	private Deal deal;
	private Layout layout;
	private Foundation foundation;
	private Tableau tableau;
	private Stock stock;

	/** Override deal as needed. */
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal()
					.append(new FilterAces())
					.append(new DealToFoundation())
                    .append(new DealToTableau(6));
		}

		return deal;
	}

	/** Override layout as needed. */
	@Override
	public Layout getLayout() {
		if (layout == null) {

            Point[] anchors = new Point[8];
            for (int idx = 0; idx < 4; idx++) {
                int y = 10 + 110 * idx;

                anchors[idx] = new Point(10, y);
                anchors[idx + 4] = new Point(400 + 10 + card_width, y);
            }

            layout = new Layout()
                    .add(SolitaireContainerTypes.Foundation, new VerticalPlacement(new Point(400, 10),
                            card_width, card_height, card_gap))
                    .add(SolitaireContainerTypes.Tableau, new CalculatedPlacement(anchors, 380, card_height)); /* 380 = 73*5 + .. */
		}

		return layout;
	}

    /**
     * A single Stock of a single deck of cards.
     *
     * @return
     */
    protected Stock getStock() {
        if (stock == null) { stock = new Stock(); }
        return stock;
    }


    /**
     * Default Foundation has four piles..
     *
     * @return
     */
    protected Foundation getFoundation() {
        if (foundation == null) {
            foundation = new Foundation();
            for (int i = 0; i < 4; i++) { foundation.add (new Pile()); }
        }
        return foundation;
    }

    /**
     * Default Tableau has eight rows, four on left and four on right
     *
     * @return
     */
    protected Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 8; i++) { tableau.add (new Row()); }
        }
        return tableau;
    }

	public Domain() {
		super ("Castle");
		init();
    }

    private void init() {
		// we intend to be solvable
		setSolvable(true);

		// register new elements for this domain (not view: that is already handled by RowView)
		registerElement(new Row());

        placeContainer(getFoundation());
        placeContainer(getTableau());
        placeContainer(getStock());


		IsEmpty isEmpty = new IsEmpty (MoveComponents.Destination);
		NextRank nextOne =  new NextRank(new TopCardOf(MoveComponents.Destination), MoveComponents.MovingCard);

		// Tableau to Tableau
		OrConstraint moveCheck = new OrConstraint(isEmpty, nextOne);

        domain.castle.SufficientFree sufficientFree= new domain.castle.SufficientFree(
                MoveComponents.MovingRow,
                MoveComponents.Source, MoveComponents.Destination,
                SolitaireContainerTypes.Tableau
        );

        Descending descend = new Descending(MoveComponents.MovingRow);

        AndConstraint and_2= new AndConstraint(new NextRank(new TopCardOf(MoveComponents.Destination),
                new BottomCardOf(MoveComponents.MovingRow)), sufficientFree);


        IfConstraint if7= new IfConstraint(isEmpty, sufficientFree, and_2 );

		RowMove MoreCardToTableau= new RowMove("MoveRow", getTableau(), descend, getTableau(), if7);
        addDragMove(MoreCardToTableau);

        IsSingle isSingle = new IsSingle(MoveComponents.MovingRow);

		AndConstraint and = new AndConstraint(
				new NextRank(new BottomCardOf(MoveComponents.MovingRow), new TopCardOf(MoveComponents.Destination)),
				new SameSuit(new BottomCardOf(MoveComponents.MovingRow), new TopCardOf(MoveComponents.Destination)));

		RowMove tableauToFoundation = new RowMove("BuildRow", getTableau(), isSingle, getFoundation(), and);
		addDragMove(tableauToFoundation);


		// When foundation is full, we are done.
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Foundation, 52);
		setLogic (state);
	}
}
