package domain.castle;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.*;
import domain.moves.SingleCardMove;
import domain.ui.CalculatedPlacement;
import domain.ui.VerticalPlacement;
import domain.ui.PlacementGenerator;

import java.awt.*;
import java.util.Iterator;


/**
 * Programmatically construct full domain model for Castle.
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

		PlacementGenerator places = new VerticalPlacement(new Point(200, 120),
				card_width, card_height, card_gap);

		Foundation found= new Foundation(places);
		found.add (new Pile());
		found.add (new Pile());
		found.add (new Pile());
		found.add (new Pile());
		containers.put(SolitaireContainerTypes.Foundation, found);

		Point[] anchors = new Point[8];
		for (int idx = 0; idx < 4; idx++) {
			int y = 110 * idx;
			anchors[idx] = new Point(10, y);
			anchors[idx + 4] = new Point(153, y);
		}
		places = new CalculatedPlacement(anchors, 380, card_height); /* 380 = 73*5 + .. */

		Tableau tableau = new Tableau(places);
		tableau.add (new Row());
		tableau.add (new Row());
		tableau.add (new Row());
		tableau.add (new Row());
		tableau.add (new Row());
		tableau.add (new Row());
		tableau.add (new Row());
		tableau.add (new Row());
		containers.put(SolitaireContainerTypes.Tableau, tableau);

		// defaults to 1 deck. And is not visible
		Stock stock = new Stock();

		containers.put(SolitaireContainerTypes.Stock, stock);

		// wins once foundation contains same number of cards as stock
		Rules rules = new Rules();


		IsEmpty isEmpty = new IsEmpty (MoveComponents.Destination);
		NextRank nextOne =  new NextRank(new TopCardOf(MoveComponents.Destination), MoveComponents.MovingCard);

		// Tableau to Tableau
		OrConstraint moveCheck = new OrConstraint(isEmpty, nextOne);

		SingleCardMove tableauToTableau = new SingleCardMove("MoveCard", tableau, tableau, moveCheck);
		rules.addDragMove(tableauToTableau);

		AndConstraint and = new AndConstraint(
				new NextRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
				new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)));

		SingleCardMove tableauToFoundation = new SingleCardMove("BuildCard", tableau, found, and);
		rules.addDragMove(tableauToFoundation);

		setRules(rules);
	}
}
