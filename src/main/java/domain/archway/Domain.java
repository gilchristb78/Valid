package domain.archway;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.MapStep;
import domain.deal.map.MapByRank;
import domain.moves.SingleCardMove;
import domain.ui.CalculatedPlacement;
import domain.ui.HorizontalPlacement;
import domain.ui.PlacementGenerator;
import domain.win.BoardState;

import java.awt.Point;

/**
 * Programmatically construct full domain model for Archway
 */
public class Domain extends Solitaire {
	public Domain() {
		super ("Archway");

		// we intend to be solvable
		setSolvable(true);

		// register new elements for this domain
		registerElement(new AcesUpPile());
		registerElement(new KingsDownPile());

		int scale = 27;

		int xs[] = new int[] { 2,  5,  2,  5};
		int ys[] = new int[] {23, 23, 27, 27};
		Point[] anchors = new Point[xs.length];
		for (int i = 0; i < xs.length; i++) {
			anchors[i] = new Point(xs[i]*scale, ys[i]*scale);
		}
		PlacementGenerator places = new CalculatedPlacement(anchors, card_width, card_height);
		Foundation acesFoundation = new Foundation();
		placeContainer (acesFoundation, places);
		for (int i = 0; i < 4; i++) {
			acesFoundation.add(new AcesUpPile());
		}
		containers.put(SolitaireContainerTypes.Foundation, acesFoundation);


		xs = new int[] {2, 2,  2, 2, 4, 10, 14, 18, 24, 26, 26, 26, 26};
		ys = new int[] {19, 15, 11, 7, 3, 1,  1,  1,  3,  7, 11, 15, 19};
		anchors = new Point[xs.length];
		for (int i = 0; i < xs.length; i++) {
			anchors[i] = new Point(xs[i]*scale, ys[i]*scale);
		}
		places = new CalculatedPlacement(anchors, card_width, card_height);
		Reserve reserve = new Reserve();
		placeContainer(reserve, places);
		for (int i = 0; i < xs.length; i++) {
			reserve.add(new Pile());
		}
		containers.put(SolitaireContainerTypes.Reserve, reserve);

		xs = new int[] {23, 26, 23, 26};
		ys = new int[] {23, 23, 27, 27};
		anchors = new Point[xs.length];
		for (int i = 0; i < xs.length; i++) {
			anchors[i] = new Point(xs[i]*scale, ys[i]*scale);
		}
		places = new CalculatedPlacement(anchors, card_width, card_height);
		Container kingsFoundation = new Container();
		placeContainer(kingsFoundation, places);
		for (int i = 0; i < 4; i++) {
			kingsFoundation.add(new KingsDownPile());
		}
		containers.put(ArchwayContainerTypes.KingsDown, kingsFoundation);

		places = new HorizontalPlacement(new Point (10*scale, 10*scale), card_width, 8*card_height, card_gap);
		Tableau tableau = new Tableau();
		placeContainer(tableau, places);
		for (int i = 0; i < 4; i++) {
			tableau.add(new Column());
		}
		containers.put(SolitaireContainerTypes.Tableau, tableau);

		// Archway has two decks.
		Stock stock = new Stock(2);
		containers.put(SolitaireContainerTypes.Stock, stock);


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
		addDragMove(
				new SingleCardMove("TableauToFoundation",
						tableau, acesFoundation, moveToAcesCondition
				)
		);

		addDragMove(
				new SingleCardMove("ReserveToFoundation",
						reserve, acesFoundation, moveToAcesCondition
				)
		);

		addDragMove(
				new SingleCardMove("TableauToKingsFoundation",
						tableau, kingsFoundation, moveToKingsCondition
				)
		);

		addDragMove(
				new SingleCardMove("ReserveToKingsFoundation",
						reserve, kingsFoundation, moveToKingsCondition
				)
		);

		addDragMove(
				new SingleCardMove("ReserveToTableau",
						reserve, tableau, new IsEmpty(MoveComponents.Destination))
		);

		// Always deny these moves.
		addDragMove(
				new SingleCardMove("TableauToTableau", tableau, tableau, new Falsehood())
		);


		// When all cards are in the AcesUp and KingsDown
		BoardState state = new BoardState();
		state.add(ArchwayContainerTypes.KingsDown, 52);
		state.add(SolitaireContainerTypes.Foundation, 52);
		setLogic (state);

		// Deal arrangement for Archway.
		// 1. Place Aces in the Aces, and Kings in the Kings
		// Filter all aces out and add back on top

        // ideally want a way to pull out four aces (by suit) but this works.

		// 1. Deal aces to aces up
        addDealStep(new FilterStep(new AndConstraint(new IsAce(DealComponents.Card),
                new IsSuit(DealComponents.Card, Card.Suits.Spades)), 1));
        addDealStep(new FilterStep(new AndConstraint(new IsAce(DealComponents.Card),
                new IsSuit(DealComponents.Card, Card.Suits.Hearts)), 1));
        addDealStep(new FilterStep(new AndConstraint(new IsAce(DealComponents.Card),
                new IsSuit(DealComponents.Card, Card.Suits.Diamonds)), 1));
		addDealStep(new FilterStep(new AndConstraint(new IsAce(DealComponents.Card),
                new IsSuit(DealComponents.Card, Card.Suits.Clubs)), 1));

		addDealStep(new DealStep(new ContainerTarget(SolitaireContainerTypes.Foundation, acesFoundation)));

        // 2. Deal kings to kings down
        addDealStep(new FilterStep(new AndConstraint(new IsKing(DealComponents.Card),
                new IsSuit(DealComponents.Card, Card.Suits.Spades)), 1));
        addDealStep(new FilterStep(new AndConstraint(new IsKing(DealComponents.Card),
                new IsSuit(DealComponents.Card, Card.Suits.Hearts)), 1));
        addDealStep(new FilterStep(new AndConstraint(new IsKing(DealComponents.Card),
                new IsSuit(DealComponents.Card, Card.Suits.Diamonds)), 1));
        addDealStep(new FilterStep(new AndConstraint(new IsKing(DealComponents.Card),
                new IsSuit(DealComponents.Card, Card.Suits.Clubs)), 1));

        addDealStep(new DealStep(new ContainerTarget(ArchwayContainerTypes.KingsDown, kingsFoundation)));

		// 3. Deal 12 cards to each of the four tableau
		addDealStep(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau, tableau),
				new Payload(12, true)));

		// 4. Remaining cards are distributed to 11 reserves, based on the decision Rank-1
		addDealStep (new MapStep(new ContainerTarget(SolitaireContainerTypes.Reserve, reserve),
				new MapByRank(), new Payload(104-8-48, true)
				));

	}
}
