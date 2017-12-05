package domain.narcotic;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
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
		super ("Narcotic");
		StockTableauLayout lay = new StockTableauLayout();

		Tableau tableau = new Tableau(lay.tableauAsPile());
		tableau.add (new Pile());
		tableau.add (new Pile());
		tableau.add (new Pile());
		tableau.add (new Pile());
		containers.put(SolitaireContainerTypes.Tableau, tableau);

		// defaults to 1 deck.
		Stock stock = new Stock(lay.stock());
		containers.put(SolitaireContainerTypes.Stock, stock);

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
		addDragMove(tableauToTableau);

		// All top cards are the same.
		AllSameRank allSameRank = new AllSameRank(SolitaireContainerTypes.Tableau);

		RemoveMultipleCardsMove tableauRemove = new RemoveMultipleCardsMove("RemoveAllCards", tableau, allSameRank);
		addPressMove(tableauRemove);

		// note: for Python implementation, still need to implement concept of DeckDealMove, as well
        // as ResetDeckMove.

		// deal four cards from Stock
		NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
		DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, tableau, new Truth());
		addPressMove(deckDeal);

		// reset deck by pulling together all cards from the piles.
		ResetDeckMove deckReset = new ResetDeckMove("ResetDeck", stock, new IsEmpty(MoveComponents.Source), tableau, new Truth());
		addPressMove(deckReset);

		// Each one gets a single faceup Card
		Payload payload = new Payload();
		addDealStep(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau, tableau), payload));
	}
}
