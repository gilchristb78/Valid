package domain.idiot;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.deal.ContainerTarget;
import domain.deal.DealStep;
import domain.deal.Payload;
import domain.moves.*;
import domain.ui.StockTableauLayout;
import domain.win.BoardState;
import domain.win.ScoreAchieved;

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
		super ("Idiot");
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

		// When only aces are left, there are 48 points.
        BoardState state = new BoardState();
        state.add(SolitaireContainerTypes.Stock, 0);
        state.add(SolitaireContainerTypes.Tableau, 4);
		setLogic (state);

		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

		// Tableau to Tableau
		SingleCardMove tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, isEmpty);
		addDragMove(tableauToTableau);

		// this special method is added by gameDomain to be accessible here.
		HigherRankSameSuit sameSuitHigherRankVisible = new HigherRankSameSuit(MoveComponents.Source);

		AndConstraint and = new AndConstraint(new NotConstraint(new IsEmpty(MoveComponents.Source)), sameSuitHigherRankVisible);

		RemoveSingleCardMove removeCardFromTableau = new RemoveSingleCardMove("RemoveCard", tableau, and);
		addClickMove(removeCardFromTableau);

		// Remove a card from the tableau? This can be optimized by a click
		// do I allow another Rule? Or reuse existing one?
		// Not sure how to deal with MOVE with a single PRESS
		// That is, this will not be the head part of a drag operation.

		// deal four cards from Stock
		NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
		DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, tableau, new Truth());
		addPressMove(deckDeal);

		// deal four cards out, one to each of the tableau
		addDealStep(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau, tableau), new Payload()));
	}
}
