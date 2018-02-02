package domain.archway;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.MapStep;
import domain.deal.map.MapByRank;
import domain.deal.steps.DealToFoundation;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.FilterAces;
import domain.moves.SingleCardMove;
import domain.ui.*;
import domain.win.BoardState;

import java.awt.Point;

/**
 * Programmatically construct full domain model for Archway
 */
public class Domain extends Solitaire {

	private Deal deal;
	private Layout layout;
    private Foundation foundation;
    private Reserve reserve;
    private Container kingsFoundation;
    private Tableau tableau;
    private Stock stock;

    public Foundation getFoundation() {
        if (foundation == null) {
            foundation = new Foundation();
            for (int i = 0; i < 4; i++) {
                foundation.add(new AcesUpPile());
            }
        }

        return foundation;
    }

    public Reserve getReserve() {
        if (reserve == null) {
            reserve = new Reserve();
            for (int i = 0; i < 13; i++) {
                reserve.add(new Pile());
            }
        }

        return reserve;
    }

    public Container getKingsFoundation() {
        if (kingsFoundation == null) {
            kingsFoundation = new Container(ArchwayContainerTypes.KingsDown);
            for (int i = 0; i < 4; i++) {
                kingsFoundation.add(new KingsDownPile());
            }
        }

        return kingsFoundation;
    }

    public Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 4; i++) {
                tableau.add(new Column());
            }
        }

        return tableau;
    }

	/** Override deal as needed. */
	@Override
	public Deal getDeal() {
        // Deal arrangement for Archway.

        // Filter all aces out and add back on top

        if (deal == null) {
			deal = new Deal()
            // 1. Place Aces in the Aces, and Kings in the Kings
			    .append(new FilterStep(new AndConstraint(new IsAce(DealComponents.Card),
					new IsSuit(DealComponents.Card, Card.Suits.Spades)), 1))
                .append(new FilterStep(new AndConstraint(new IsAce(DealComponents.Card),
					new IsSuit(DealComponents.Card, Card.Suits.Hearts)), 1))
			    .append(new FilterStep(new AndConstraint(new IsAce(DealComponents.Card),
					new IsSuit(DealComponents.Card, Card.Suits.Diamonds)), 1))
                .append(new FilterStep(new AndConstraint(new IsAce(DealComponents.Card),
					new IsSuit(DealComponents.Card, Card.Suits.Clubs)), 1))

                .append(new DealToFoundation())

			// 2. Deal kings to kings down
                .append(new FilterStep(new AndConstraint(new IsKing(DealComponents.Card),
					new IsSuit(DealComponents.Card, Card.Suits.Spades)), 1))
                .append(new FilterStep(new AndConstraint(new IsKing(DealComponents.Card),
					new IsSuit(DealComponents.Card, Card.Suits.Hearts)), 1))
                .append(new FilterStep(new AndConstraint(new IsKing(DealComponents.Card),
					new IsSuit(DealComponents.Card, Card.Suits.Diamonds)), 1))
                .append(new FilterStep(new AndConstraint(new IsKing(DealComponents.Card),
					new IsSuit(DealComponents.Card, Card.Suits.Clubs)), 1))

			    .append(new DealStep(new ContainerTarget(ArchwayContainerTypes.KingsDown)))

			// 3. Deal 12 cards to each of the four tableau
			    .append(new DealToTableau(12))

			// 4. Remaining cards are distributed to 11 reserves, based on the decision Rank-1
			    .append (new MapStep(new ContainerTarget(SolitaireContainerTypes.Reserve),
					new MapByRank(), new Payload(104-8-48, true)
			));
		}

		return deal;
	}

    /** Override layout as needed. */
	@Override
	public Layout getLayout() {
		if (layout == null) {
			layout = new ArchwayLayout();  // TODO: FIX ME!
		}

		return layout;
	}

	public Stock getStock() {
	    if (stock == null) {
            // Archway has two decks.
            stock = new Stock(2);
        }

        return stock;
    }

	public Domain() {
		super ("Archway");
		init();
	}

	private void init() {
		// we intend to be solvable
		setSolvable(true);

		// register new elements for this domain
		registerElementAndView(new AcesUpPile(), new View("AcesUpPileView", "PileView", "AcesUpPile"));
		registerElementAndView(new KingsDownPile(), new View("KingsDownPileView", "PileView", "KingsDownPile"));

        placeContainer(getFoundation());
        placeContainer(getReserve());
        placeContainer(getKingsFoundation());
        placeContainer(getTableau());
        placeContainer(getStock());

		// Get KlondikeDomain objects from Solitaire Object.

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
						tableau, foundation, moveToAcesCondition
				)
		);

		addDragMove(
				new SingleCardMove("ReserveToFoundation",
						reserve, foundation, moveToAcesCondition
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
	}
}
