package domain.freeCell;

import domain.*;

import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.moves.*;
import domain.ui.Layout;
import domain.ui.ReserveFoundationTableauLayout;
import domain.ui.View;
import domain.win.BoardState;


/**
 * Programmatically construct full domain model for FreeCell.
 *
 * DoubleFreeCell (and TripleFreeCell) all require wrap-around for NextRank. So even for the
 * regular FreeCell, we replace NextRank with NextRankWrapAround, but only on BUILD moves
 */
public class FreeCellDomain extends Solitaire implements VariationPoints {

	private Deal deal;
	private Layout layout;
	private Foundation foundation;
	private Reserve reserve;
	private Tableau tableau;
	private Stock stock;


    /** Determines the logic of moving given entity to an empty Tableau. Always allow in standard FreeCell. */
    @Override
    public Constraint buildOnEmptyTableau(MoveInformation bottom) {
        return new Truth();
    }

    @Override
	public Constraint buildOnFoundation(MoveInformation bottom) {
		return new AndConstraint(
				new NextRankWrapAround(bottom, new TopCardOf(MoveComponents.Destination)),
				new SameSuit(bottom, new TopCardOf(MoveComponents.Destination)));
	}

	/** Override deal as needed. */
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal()
					.append(new DealToTableau(6));

            // first four columns get a single card
            for (int pile = 0; pile < 4; pile++) {
                deal.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, pile), new Payload()));
            }
        }

		return deal;
	}

	/** Override layout as needed. */
	@Override
	public Layout getLayout() {
		if (layout == null) {
			layout = new ReserveFoundationTableauLayout(getReserve(), getFoundation(), getTableau());
        }

		return layout;
	}

    /**
     * Default Reserve has five Free piles..
     *
     * @return
     */
    protected Reserve getReserve() {
        if (reserve == null) {
            reserve = new Reserve();
            for (int i = 0; i < 4; i++) { reserve.add (new FreePile()); }
        }
        return reserve;
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
     * Default Tableau has eight columns
     *
     * @return
     */
    protected Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 8; i++) { tableau.add (new Column()); }
        }
        return tableau;
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

	public FreeCellDomain() {
		this ("FreeCell");
	}


    protected FreeCellDomain(String name) {
        super(name);
        init();
    }

    private void init() {

		// we intend to be solvable
		setSolvable(true);

		// register new elements for this domain
		registerElementAndView(new FreePile(), new View("FreePileView", "PileView", "FreePile"));
		registerElementAndView(new HomePile(), new View("HomePileView", "PileView", "HomePile"));

        placeContainer(getFoundation());
        placeContainer(getReserve());
        placeContainer(getTableau());
        placeContainer(getStock());

		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

		// FreePile to FreePile
		SingleCardMove freePileToFreePile = new SingleCardMove("ShuffleFreePile", getReserve(), getReserve(), isEmpty);
		addDragMove(freePileToFreePile);

		// Column To Free Pile Logic
		ColumnMove columnToFreePileMove = new ColumnMove("PlaceColumn",
				getTableau(),   new IsSingle(MoveComponents.MovingColumn),
                getReserve(),   isEmpty);
		addDragMove(columnToFreePileMove);

		// Column To Home Pile logic. Just grab first column
		IfConstraint if2 = new IfConstraint(isEmpty,
            new IsAce(new TopCardOf(MoveComponents.MovingColumn)),
            buildOnFoundation(new BottomCardOf(MoveComponents.MovingColumn)));

		ColumnMove columnToHomePile = new ColumnMove("BuildColumn",
                getTableau(),       new IsSingle(MoveComponents.MovingColumn),
				getFoundation(),    if2);
		addDragMove(columnToHomePile);

		// FreePile to HomePile
		Element aCard = new Card();
		NotConstraint nonEmpty = new NotConstraint(new IsEmpty(MoveComponents.Destination));
		IfConstraint if3 = new IfConstraint(isEmpty,
                new IsAce(MoveComponents.MovingCard),
                buildOnFoundation(MoveComponents.MovingCard));
//                new AndConstraint (new NextRankWrapAround(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
//                                   new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination))));

		SingleCardMove freePileToHomePile = new SingleCardMove("BuildFreePileCard", getReserve(), getFoundation(), if3);
		addDragMove(freePileToHomePile);

		// FreePile to Column.
        AndConstraint if5_inner = new AndConstraint(new OppositeColor(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
                                                   new NextRank(new TopCardOf(MoveComponents.Destination), MoveComponents.MovingCard));

		IfConstraint if5 = new IfConstraint(isEmpty, buildOnEmptyTableau(MoveComponents.MovingCard), if5_inner);
		SingleCardMove freePileToColumnPile = new SingleCardMove("PlaceFreePileCard", getReserve(), getTableau(), if5);
		addDragMove(freePileToColumnPile);

		// column to column
		Descending descend = new Descending(MoveComponents.MovingColumn);
		AlternatingColors alternating = new AlternatingColors(MoveComponents.MovingColumn);

		// If destination is EMPTY, then can't count it as vacant
		// If source is EMPTY (b/c move created it) then can't count it as vacant either.
		//val oneIsEmpty = new OrConstraint(isEmpty, new IsEmpty ("source"))
        SufficientFree sufficientFree = new SufficientFree(
        		MoveComponents.MovingColumn,
				MoveComponents.Source, MoveComponents.Destination,
				SolitaireContainerTypes.Reserve, SolitaireContainerTypes.Tableau);

		AndConstraint if4_inner = new AndConstraint(
                new OppositeColor(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination)),
                new NextRank(new TopCardOf(MoveComponents.Destination), new BottomCardOf(MoveComponents.MovingColumn)),
                sufficientFree);

		IfConstraint if4 = new IfConstraint(isEmpty, new AndConstraint(buildOnEmptyTableau(new BottomCardOf(MoveComponents.MovingColumn)), sufficientFree), if4_inner);

		ColumnMove columnToColumn = new ColumnMove("MoveColumn",
                getTableau(),    new AndConstraint(descend, alternating),
                getTableau(),    if4);
		addDragMove(columnToColumn);

		// When foundation is full, we are done. Be aware there may be multi-deck games.
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Foundation, getStock().numDecks*52);
		setLogic (state);
	}
}
