package domain.freeCell;

import java.util.*;
import domain.*;

import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.ContainerTarget;
import domain.deal.DealStep;
import domain.deal.ElementTarget;
import domain.deal.Payload;
import domain.moves.*;
import domain.ui.ReserveFoundationTableauLayout;
import domain.win.BoardState;


/**
 * Programmatically construct full domain model for FreeCell.
 */
public class Domain extends Solitaire {

	public static void main (String[] args) {
		Domain sfc = new Domain();

		System.out.println("Available Moves:");
		for (Iterator<Move> it = sfc.getRules().drags(); it.hasNext(); ) {
			System.out.println("  " + it.next());
		}
	}

	public Domain() {
		super ("FreeCell");

		// we intend to be solvable
		setSolvable(true);

		// register new elements for this domain
		registerElement(new FreePile());
		registerElement(new HomePile());

		ReserveFoundationTableauLayout lay = new ReserveFoundationTableauLayout();

		Foundation found = new Foundation();
		found.add (new HomePile());
		found.add (new HomePile());
		found.add (new HomePile());
		found.add (new HomePile());
		placeContainer(found, lay.foundation());
		containers.put(SolitaireContainerTypes.Foundation, found);

		Reserve reserve = new Reserve();
		reserve.add (new FreePile());	
		reserve.add (new FreePile());	
		reserve.add (new FreePile());	
		reserve.add (new FreePile());
		placeContainer(reserve, lay.reserve());
		containers.put(SolitaireContainerTypes.Reserve, reserve);

		Tableau tableau = new Tableau();
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		placeContainer(tableau, lay.tableau());
		containers.put(SolitaireContainerTypes.Tableau, tableau);

		// defaults to 1 deck. Note this container is not placed and remains invisible
		Stock stock = new Stock();
		containers.put(SolitaireContainerTypes.Stock, stock);

		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

		// FreePile to FreePile
		SingleCardMove freePileToFreePile = new SingleCardMove("ShuffleFreePile", reserve, reserve, isEmpty);
		addDragMove(freePileToFreePile);

		// Column To Free Pile Logic
		ColumnMove columnToFreePileMove = new ColumnMove("PlaceColumn",
				tableau,   new IsSingle(MoveComponents.MovingColumn),
				reserve,   isEmpty);
		addDragMove(columnToFreePileMove);

		// Column To Home Pile logic. Just grab first column
		Element aCol = tableau.iterator().next();
		IfConstraint if2 = new IfConstraint(isEmpty,
            new IsAce(new TopCardOf(MoveComponents.MovingColumn)),
            new AndConstraint(
                  new NextRank(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination)),
                  new SameSuit(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination))));

		ColumnMove columnToHomePile = new ColumnMove("BuildColumn",
				tableau,  new IsSingle(MoveComponents.MovingColumn),
				found,    if2);
		addDragMove(columnToHomePile);

		// FreePile to HomePile
		Element aCard = new Card();
		NotConstraint nonEmpty = new NotConstraint(new IsEmpty(MoveComponents.Destination));
		IfConstraint if3 = new IfConstraint(isEmpty,
                new IsAce(MoveComponents.MovingCard),
                new AndConstraint (new NextRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
                                   new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination))));

		SingleCardMove freePileToHomePile = new SingleCardMove("BuildFreePileCard", reserve, found, if3);
		addDragMove(freePileToHomePile);

		// FreePile to Column.
        AndConstraint if5_inner = new AndConstraint(new OppositeColor(MoveComponents.MovingCard, new BottomCardOf(MoveComponents.Destination)),
                                                   new NextRank(new BottomCardOf(MoveComponents.Destination), MoveComponents.MovingCard));

		IfConstraint if5 = new IfConstraint(isEmpty, new Truth(), if5_inner);
		SingleCardMove freePileToColumnPile = new SingleCardMove("PlaceFreePileCard", reserve, tableau, if5);
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

		IfConstraint if4 = new IfConstraint(isEmpty, sufficientFree, if4_inner);

		ColumnMove columnToColumn = new ColumnMove("MoveColumn",
				tableau,    new AndConstraint(descend, alternating),
				tableau,    if4);
		addDragMove(columnToColumn);

		// Eight tableau, each gets six cards, face up
		addDealStep(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau, tableau),
                new Payload(6, true)));

        // first four columns get a single card
		for (int pile = 0; pile < 4; pile++) {
			addDealStep(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, tableau, pile), new Payload()));
		}

		// When foundation is full, we are done.
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Foundation, 52);
		setLogic (state);

	}
}
