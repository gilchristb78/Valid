package domain.castle;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.*;
import domain.deal.*;
import domain.moves.RowMove;
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
		super ("BeleagueredCastle");
		PlacementGenerator places = new VerticalPlacement(new Point(400, 10),
				card_width, card_height, card_gap);

		Foundation found= new Foundation(places);
		found.add (new Pile());
		found.add (new Pile());
		found.add (new Pile());
		found.add (new Pile());
		containers.put(SolitaireContainerTypes.Foundation, found);

		Point[] anchors = new Point[8];
		for (int idx = 0; idx < 4; idx++) {
			int y = 10 + 110 * idx;

			anchors[idx] = new Point(10, y);
			anchors[idx + 4] = new Point(400 + 10 + card_width, y);
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

        domain.castle.SufficientFree sufficientFree= new domain.castle.SufficientFree(
                MoveComponents.MovingRow,
                MoveComponents.Source, MoveComponents.Destination,
                SolitaireContainerTypes.Tableau
        );

        Descending descend = new Descending(MoveComponents.MovingRow);

        AndConstraint and_2= new AndConstraint(new NextRank(new TopCardOf(MoveComponents.Destination),
                new BottomCardOf(MoveComponents.MovingRow)), sufficientFree);


        IfConstraint if7= new IfConstraint(isEmpty, sufficientFree, and_2 );

		RowMove MoreCardToTableau= new RowMove("MoveRow", tableau, descend, tableau, if7);
        rules.addDragMove(MoreCardToTableau);

        IsSingle isSingle = new IsSingle(MoveComponents.MovingRow);

		AndConstraint and = new AndConstraint(
				new NextRank(new BottomCardOf(MoveComponents.MovingRow), new TopCardOf(MoveComponents.Destination)),
				new SameSuit(new BottomCardOf(MoveComponents.MovingRow), new TopCardOf(MoveComponents.Destination)));

		RowMove tableauToFoundation = new RowMove("BuildRow", tableau, isSingle, found, and);
		rules.addDragMove(tableauToFoundation);

		setRules(rules);

		Deal d = new Deal();

		// Filter all aces out and add back on top
		d.add(new FilterStep(new IsAce(DealComponents.Card)));

		// deal aces first
		d.add(new DealStep(new ContainerTarget(SolitaireContainerTypes.Foundation, found)));

		// Each tableau gets a single card, six times.
		for (int i = 0; i < 6; i++) {
			d.add(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau, tableau)));
		}

		setDeal(d);
	}
}
