package domain.castle;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.*;
import domain.freeCell.SufficientFree;
import domain.moves.ColumnMove;
import domain.moves.SingleCardMove;
import domain.ui.CalculatedPlacement;
import domain.ui.VerticalPlacement;
import domain.ui.PlacementGenerator;
import scala.tools.nsc.transform.patmat.Logic;

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


		//I add for tableau to tableau because there could be several empty spots
        //move single card

//        ColumnMove SingleCardToTableau = new ColumnMove("MoveColumn_1", tableau,
//                new IsSingle(MoveComponents.MovingColumn), tableau, moveCheck);
//        rules.addDragMove(SingleCardToTableau);

        // move more cards

        NotConstraint nonSingle = new NotConstraint(new IsSingle(MoveComponents.MovingColumn));

        domain.castle.SufficientFree sufficientFree= new domain.castle.SufficientFree(
                MoveComponents.MovingColumn,
                MoveComponents.Source, MoveComponents.Destination,
                SolitaireContainerTypes.Tableau
        );

        Descending descend = new Descending(MoveComponents.MovingColumn);

        AndConstraint and_1= new AndConstraint(nonSingle,descend);

        AndConstraint and_2= new AndConstraint(new NextRank(new TopCardOf(MoveComponents.Destination),
                new BottomCardOf(MoveComponents.MovingColumn)), sufficientFree);

        // number of moving cards== sufficientFree.column-1  ???

        IfConstraint if7= new IfConstraint(isEmpty, sufficientFree, and_2 );

        ColumnMove MoreCardToTableau= new ColumnMove("MoveColumn", tableau, and_1,tableau, if7);
        rules.addDragMove(MoreCardToTableau);



        //domain.castle.SufficientFree sufficientFree= new domain.castle.SufficientFree(
               // MoveComponents.MovingColumn,
                //MoveComponents.Source, MoveComponents.Destination,
               // SolitaireContainerTypes.Tableau
       // );




        // fix the src constraint; won't always be Truth
		//ColumnMove tableauToTableau = new ColumnMove("MoveColumn", tableau, new Truth(), tableau, moveCheck);
		//rules.addDragMove(tableauToTableau);



		AndConstraint and = new AndConstraint(
				new NextRank(MoveComponents.MovingColumn, new TopCardOf(MoveComponents.Destination)),
				new SameSuit(MoveComponents.MovingColumn, new TopCardOf(MoveComponents.Destination)));

		ColumnMove tableauToFoundation = new ColumnMove("BuildColumn", tableau, new Truth(), found, and);
		rules.addDragMove(tableauToFoundation);

		setRules(rules);
	}
}
