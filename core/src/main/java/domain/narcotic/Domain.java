package domain.narcotic;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.moves.*;
import domain.ui.StockTableauLayout;

import java.util.Iterator;


/**
 * Programmatically construct full domain model for Narcotic.
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
//
//		val semanticType: Type =
//				'Solitaire ('Tableau ('None)) :&: 'Solitaire ('Layout ('None)) :&: 'Solitaire ('Rules('None)) =>:
//		'Tableau ('Valid :&: 'Four :&: 'Pile) =>:
//		'Stock ('Valid :&: 'One :&: 'Deck) =>:
//		'Solitaire ('Structure ('Narcotic))

//
		StockTableauLayout lay = new StockTableauLayout();

		Tableau tableau = new Tableau(lay.tableau());
		tableau.add (new Pile());
		tableau.add (new Pile());
		tableau.add (new Pile());
		tableau.add (new Pile());
		containers.put(SolitaireContainerTypes.Tableau, tableau);

		// defaults to 1 deck.
		Stock stock = new Stock(lay.stock());
		containers.put(SolitaireContainerTypes.Stock, stock);

		// wins once foundation contains same number of cards as stock
		Rules rules = new Rules();

		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

		IfConstraint if_move = new IfConstraint(isEmpty);

		// Tableau to Tableau. Can move a card to the left if it is
		// going to a non-empty pile whose top card is the same rank
		// as moving card, and which is to the left of the source.
		ToLeftOf toLeftOf = new ToLeftOf(MoveComponents.Destination, MoveComponents.Source);
		// new BooleanExpression("((org.combinators.solitaire.narcotic.Narcotic)game).toLeftOf(destination, source)");

		SameRank sameRank = new SameRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination));

		AndConstraint tt_move = new AndConstraint(new NotConstraint(new IsEmpty(MoveComponents.Destination)),
				toLeftOf, sameRank);

		SingleCardMove tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, tt_move);
		rules.addDragMove(tableauToTableau);

		/** Awkward constraint. Requires downcast to access method. Perhaps move into move class?*/
		AllSameRank allSameRank = new AllSameRank(SolitaireContainerTypes.Tableau);

		//		new BooleanExpression("((org.combinators.solitaire.narcotic.Narcotic)game).allSameRank()");

		RemoveMultipleCardsMove tableauRemove = new RemoveMultipleCardsMove("RemoveAllCards", tableau, allSameRank);
		rules.addPressMove(tableauRemove);

		// deal four cards from Stock
		NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
		DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, tableau, deck_move);
		rules.addPressMove(deckDeal);

		// reset deck if empty. Move is triggered by press on stock.
		// this creates DeckToPile, as in the above DeckDealMove.
		ResetDeckMove deckReset = new ResetDeckMove("ResetDeck", stock, tableau, new IsEmpty(MoveComponents.Source));
		rules.addPressMove(deckReset);

		setRules(rules);

		// Not doing rules since changing to AST-based logic

	}
}
