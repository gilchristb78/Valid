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
		for (Iterator<Move> it = sfc.getRules().drags(); it.hasNext(); ) {
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

		// Not doing rules since changing to AST-based logic

	}
}
