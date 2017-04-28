package domain.freeCell;

import java.util.*;
import domain.*;
import domain.constraints.*;
import domain.moves.*;

/**
 * Programmatically construct full domain model for FreeCell.
 */
public class SampleFreeCell extends Solitaire {

	public static void main (String[] args) {
		SampleFreeCell sfc = new SampleFreeCell();
		
		// output info
		for (Container c : sfc) {
			System.out.println (c);
			for (Element e : c) {
				System.out.println("  " + e);
			}
		}
		
		System.out.println("Win:" + sfc.getRules().getLogic());
		
		System.out.println("Available Moves:");
		for (Iterator<Move> it = sfc.getRules().getMoves(); it.hasNext(); ) {
			System.out.println("  " + it.next());
		}
	}

	public SampleFreeCell () {
		Foundation found = new Foundation();
		found.add (new HomePile());
		found.add (new HomePile());
		found.add (new HomePile());
		found.add (new HomePile());
		setFoundation(found);

		Reserve reserve = new Reserve();
		reserve.add (new FreePile());	
		reserve.add (new FreePile());	
		reserve.add (new FreePile());	
		reserve.add (new FreePile());	
		setReserve(reserve);

		Tableau tableau = new Tableau();
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		setTableau(tableau);

		// defaults to 1 deck.
		Stock stock = new Stock();
		setStock(stock);

		// wins once foundation contains same number of cards as stock
		Rules rules = new Rules();
		FoundationWinLogic win = new FoundationWinLogic();
		rules.setLogic(win);
		setRules(rules);

		// valid moves
		Move m;
		Constraint c1 = new AlternatingDescending();
		Constraint c2 = new BaseCardOneHigherOppositeColor(c1);
		Constraint c2a = new SufficientFree(c2);
		m = new ColumnMove(tableau, tableau, c2a);
		rules.addMove(m);
		m = new SingleCardMove(reserve, tableau, c2a);  // still applies even though a single card
		rules.addMove(m);

		// can move a single card from tableau to reserve, if empty
		// can move a single card from reserve to reserve, if empty
		Constraint c3 = new ElementEmpty();
		m = new SingleCardMove(tableau, reserve, c3);
		rules.addMove(m);
		m = new SingleCardMove(reserve, reserve, c3);
		rules.addMove(m);

		/**
		 * not clear what is meant by 'isEmpty' -- the source or the target?
		 *
		 * If target is empty, then Ace allowed
		 * If target is not empty, then single card one higher in rank by suit
		 */
		Constraint c4a = new IsAce();
		Constraint c4b = new ElementEmpty(c4a);

		Constraint c4c = new NotConstraint(new ElementEmpty());
		Constraint c4d = new OneHigherRankSameSuit(c4c);

		Constraint c4 = new OrConstraint(c4b, c4d);
		m = new SingleCardMove(tableau, found, c4);
		rules.addMove(m);
		m = new SingleCardMove(reserve, found, c4);

	}
}