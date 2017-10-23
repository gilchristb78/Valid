package domain.idiot;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.moves.*;
import domain.ui.StockTableauLayout;

import java.util.Iterator;


/**
 * Programmatically construct full domain model for Idiot.
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

		StockTableauLayout lay = new StockTableauLayout();

		Tableau tableau = new Tableau(lay.tableau());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		tableau.add (new Column());
		containers.put(SolitaireContainerTypes.Tableau, tableau);

		// defaults to 1 deck.
		Stock stock = new Stock(lay.stock());
		containers.put(SolitaireContainerTypes.Stock, stock);

		// wins once foundation contains same number of cards as stock
		Rules rules = new Rules();

		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

		// Tableau to Tableau
		SingleCardMove tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, isEmpty);
		rules.addDragMove(tableauToTableau);

		// this special method is added by gameDomain to be accessible here.
//		BooleanExpression sameSuitHigherRankVisible =
//				new BooleanExpression("((org.combinators.solitaire.idiot.Idiot)game).isHigher((Column)source)");
		HigherRankSameSuit sameSuitHigherRankVisible = new HigherRankSameSuit(MoveComponents.Source);

		AndConstraint and = new AndConstraint(new NotConstraint(new IsEmpty(MoveComponents.Source)), sameSuitHigherRankVisible);

		RemoveSingleCardMove removeCardFromTableau = new RemoveSingleCardMove("RemoveCard", tableau, and);
		rules.addClickMove(removeCardFromTableau);

		// Remove a card from the tableau? This can be optimized by a click
		// do I allow another Rule? Or reuse existing one?
		// Not sure how to deal with MOVE with a single PRESS
		// That is, this will not be the head part of a drag operation.

		// deal four cards from Stock
		NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
		DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, tableau, deck_move);
		rules.addPressMove(deckDeal);
		setRules(rules);
	}
}
