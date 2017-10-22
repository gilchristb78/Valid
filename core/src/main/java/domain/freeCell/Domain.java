package domain.freeCell;

import java.util.*;
import domain.*;

import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.moves.*;
import domain.ui.ReserveFoundationTableauLayout;


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

		ReserveFoundationTableauLayout lay = new ReserveFoundationTableauLayout();

		Foundation found = new Foundation(lay.foundation());
		found.add (new HomePile());
		found.add (new HomePile());
		found.add (new HomePile());
		found.add (new HomePile());
		containers.put(SolitaireContainerTypes.Foundation, found);

		Reserve reserve = new Reserve(lay.reserve());
		reserve.add (new FreePile());	
		reserve.add (new FreePile());	
		reserve.add (new FreePile());	
		reserve.add (new FreePile());
		containers.put(SolitaireContainerTypes.Reserve, reserve);

		Tableau tableau = new Tableau(lay.tableau());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		containers.put(SolitaireContainerTypes.Tableau, tableau);

		// defaults to 1 deck.
		Stock stock = new Stock();
		containers.put(SolitaireContainerTypes.Stock, stock);

		// wins once foundation contains same number of cards as stock
		Rules rules = new Rules();

		// Expression Problem Trivially Encoding (SPLASH 2015).

		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

		// FreePile to FreePile
		SingleCardMove freePileToFreePile = new SingleCardMove("ShuffleFreePile", reserve, reserve, isEmpty);
		rules.addDragMove(freePileToFreePile);

		// Column To Free Pile Logic
		IsSingle isSingle = new IsSingle(MoveComponents.MovingColumn);
        AndConstraint if1 = new AndConstraint(isEmpty, isSingle);

		ColumnMove columnToFreePileMove = new ColumnMove("PlaceColumn", tableau, reserve, if1);
		rules.addDragMove(columnToFreePileMove);

		// Column To Home Pile logic. Just grab first column
		Element aCol = tableau.iterator().next();
		IfConstraint if2 = new IfConstraint(isEmpty,
            new IsAce(new TopCardOf(MoveComponents.MovingColumn)),
            new AndConstraint(new IsSingle(MoveComponents.MovingColumn),
                  new NextRank(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination)),
                  new SameSuit(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination))));

		ColumnMove columnToHomePile = new ColumnMove("BuildColumn", tableau, found, if2);
		rules.addDragMove(columnToHomePile);

		// FreePile to HomePile
		Element aCard = new Card();
		NotConstraint nonEmpty = new NotConstraint(new IsEmpty(MoveComponents.Destination));
		IfConstraint if3 = new IfConstraint(isEmpty,
                new IsAce(MoveComponents.MovingCard),
                new AndConstraint (new NextRank(MoveComponents.MovingCard, new BottomCardOf(MoveComponents.Destination)),
                                   new SameSuit(MoveComponents.MovingCard, new BottomCardOf(MoveComponents.Destination))));

		SingleCardMove freePileToHomePile = new SingleCardMove("BuildFreePileCard", reserve, found, if3);
		rules.addDragMove(freePileToHomePile);

		// FreePile to Column.
        AndConstraint if5_inner = new AndConstraint(new OppositeColor(MoveComponents.MovingCard, new BottomCardOf(MoveComponents.Destination)),
                                                   new NextRank(new BottomCardOf(MoveComponents.Destination), MoveComponents.MovingCard));

		IfConstraint if5 = new IfConstraint(isEmpty, new Truth(), if5_inner);
		SingleCardMove freePileToColumnPile = new SingleCardMove("PlaceFreePileCard", reserve, tableau, if5);
		rules.addDragMove(freePileToColumnPile);

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

		AndConstraint if4 = new AndConstraint(descend, alternating,
						new IfConstraint(isEmpty, sufficientFree, if4_inner));

		ColumnMove columnToColumn = new ColumnMove("MoveColumn", tableau, tableau, if4);
		rules.addDragMove(columnToColumn);

		setRules(rules);

		// Not doing rules since changing to AST-based logic

	}
}
