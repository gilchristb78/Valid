package domain.archway;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.moves.SingleCardMove;
import domain.ui.CalculatedPlacement;
import domain.ui.HorizontalPlacement;
import domain.ui.PlacementGenerator;

import java.awt.Point;

/**
 * Programmatically construct full domain model for Archway
 */
public class Domain extends Solitaire {
	public Domain() {
		int scale = 27;

		int xs[] = new int[] { 2,  5,  2,  5};
		int ys[] = new int[] {19, 19, 23, 23};
		Point[] anchors = new Point[xs.length];
		for (int i = 0; i < xs.length; i++) {
			anchors[i] = new Point(xs[i]*scale, ys[i]*scale);
		}
		PlacementGenerator places = new CalculatedPlacement(anchors, card_width, card_height);
		Foundation acesFoundation = new Foundation(places);
		for (int i = 0; i < 4; i++) {
			acesFoundation.add(new AcesUpPile());
		}
		containers.put(SolitaireContainerTypes.Foundation, acesFoundation);


		xs = new int[] {2,  2, 2, 4, 10, 14, 18, 24, 26, 26, 26};
		ys = new int[] {15, 11, 7, 3, 1,  1,  1,  3,  7, 11, 15};
		anchors = new Point[xs.length];
		for (int i = 0; i < xs.length; i++) {
			anchors[i] = new Point(xs[i]*scale, ys[i]*scale);
		}
		places = new CalculatedPlacement(anchors, card_width, card_height);
		Reserve reserve = new Reserve(places);
		for (int i = 0; i < 11; i++) {
			reserve.add(new Pile());
		}
		containers.put(SolitaireContainerTypes.Reserve, reserve);


		xs = new int[] {23, 26, 23, 26};
		ys = new int[] {19, 19, 23, 23};
		anchors = new Point[xs.length];
		for (int i = 0; i < xs.length; i++) {
			anchors[i] = new Point(xs[i]*scale, ys[i]*scale);
		}
		places = new CalculatedPlacement(anchors, card_width, card_height);
		Container kingsFoundation = new Container(places);
		for (int i = 0; i < 4; i++) {
			kingsFoundation.add(new KingsDownPile());
		}
		containers.put(ArchwayContainerTypes.KingsDown, kingsFoundation);

		places = new HorizontalPlacement(new Point (10*scale, 10*scale), card_width, 8*card_height, card_gap);
		Tableau tableau = new Tableau(places);
		for (int i = 0; i < 4; i++) {
			tableau.add(new Column());
		}
		containers.put(SolitaireContainerTypes.Tableau, tableau);

		// Archway has two decks.
		Stock stock = new Stock(2);
		containers.put(SolitaireContainerTypes.Stock, stock);

		// wins once foundation contains same number of cards as stock
		Rules rules = new Rules();

		// Get Domain objects from Solitaire Object.

      /* Contraint saying if the moving card has the same suit as the top-facing destination card. */
		// Note that these are real fields.
		SameSuit sameSuit = new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination));

      /* A Card can move to the Aces Foundation if the moving card is
       * one rank higher and has the same suit.
       */
		AndConstraint moveToAcesCondition = new AndConstraint(
						new NextRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
						sameSuit);

      /* A Card can move to the Kings Foundation if the moving card is
       * one rank lower and has the same suit.
       */
		AndConstraint moveToKingsCondition = new AndConstraint(
						new NextRank(new TopCardOf(MoveComponents.Destination), MoveComponents.MovingCard),
						sameSuit);

      /* Add Rules */

		// Note that the string argument becomes a real classname.
		rules.addDragMove(
				new SingleCardMove("TableauToFoundation",
						tableau, acesFoundation, moveToAcesCondition
				)
		);

		rules.addDragMove(
				new SingleCardMove("ReserveToFoundation",
						reserve, acesFoundation, moveToAcesCondition
				)
		);

		rules.addDragMove(
				new SingleCardMove("TableauToKingsFoundation",
						tableau, kingsFoundation, moveToKingsCondition
				)
		);

		rules.addDragMove(
				new SingleCardMove("ReserveToKingsFoundation",
						reserve, kingsFoundation, moveToKingsCondition
				)
		);

		rules.addDragMove(
				new SingleCardMove("ReserveToTableau",
						reserve, tableau, new IsEmpty(MoveComponents.Destination))
		);

		// Always deny these moves.
		rules.addDragMove(
				new SingleCardMove("TableauToTableau", tableau, tableau, new Falsehood())
		);

		setRules(rules);

		// Not doing rules since changing to AST-based logic

	}
}
