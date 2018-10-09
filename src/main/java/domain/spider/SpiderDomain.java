package domain.spider;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.moves.ColumnMove;
import domain.moves.DeckDealMove;
import domain.moves.*;
import domain.ui.*;
/*
import domain.ui.Layout;
import domain.ui.VerticalPlacement;
import domain.ui.HorizontalPlacement;
*/
import domain.win.BoardState;

import java.awt.*;

/**
 * Programmatically construct full domain model for "Hello-World"  Spider variation
 */
public class SpiderDomain extends Solitaire implements VariationPoints{


	/** Override deal, depends on variation. */
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal();

			// Deal 5 face-down cards to the first four piles, 5 to the rest
			// don't forget zero-based indexing
			for (int colNum = 0; colNum < 4; colNum++) {
				deal.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, colNum), new Payload(5, false)));
			}
			for (int colNum = 4; colNum < 10; colNum++) {
				deal.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, colNum), new Payload(4, false)));
			}

			// Finally, each pile gets one face-up card
			deal.append(new DealToTableau());
		}

		return deal;
	}

	/** Override Stock, may change with variation */
	@Override
	public Stock getStock() {
		if (stock == null) {
			// Spider has two decks
			stock = new Stock(2);
		}
		return stock;
	}

	/** Override Tableau, may change with variation */
	@Override
	public Tableau getTableau() {
		if (tableau == null) {
			tableau = new Tableau();
			for (int i = 0; i < 10; i++) { tableau.add (new BuildablePile()); }//Column()); }
		}
		return tableau;
	}

	/**
	 * Determine requirements for moving a column of cards
	 *
	 * @return
	 */
	@Override
	public Constraint moveColumn(){
		AllSameSuit sameSuit = new AllSameSuit(MoveComponents.MovingColumn);
		Descending descend = new Descending(MoveComponents.MovingColumn);
		return new AndConstraint(descend, sameSuit);
	}

	/**
	 * Anything can be placed on an empty tableau.
	 * @param bottom
	 * @return
	 */
	public Constraint buildOnEmptyTableau(MoveInformation bottom) {
		return new Truth();
	}


//	private Deal deal;
	protected Deal deal;

	private Layout layout;
	private Foundation foundation;

	//private Tableau tableau;
	protected Tableau tableau;

	//private Stock stock;
	protected Stock stock;

    public Foundation getFoundation() {
    	if (foundation == null ) {
    		foundation = new Foundation();
    		for (int i = 0; i < 8; i++) {
    			foundation.add(new Pile());
			}
		}

		return foundation;
	}

    /** Override layout as needed. */
	@Override
	public Layout getLayout() {
		if (layout == null) {
			layout = new Layout()
					.add(SolitaireContainerTypes.Stock, new HorizontalPlacement(new Point(10, 10),
							card_width, card_height, card_gap))
					.add(SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point(50, 150),
							card_width, card_height*10, card_gap))
					.add(SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(210, 10),
							card_width, card_height, card_gap));
		}
		return layout;
	}

	public SpiderDomain(){ this("Spider");}

	/** Only here for pass-through to subclasses. */
	protected SpiderDomain(String name) {
		super (name);
		init();
	}

	private void init() {
		// we intend to be solvable
		//setSolvable(true); //TODO

		placeContainer(getFoundation());
        placeContainer(getTableau());
        placeContainer(getStock());

		// Rules of Spider defined below
		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);
		BottomCardOf bottomMoving = new BottomCardOf(MoveComponents.MovingColumn);
        TopCardOf topMoving = new TopCardOf(MoveComponents.MovingColumn);
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
		TopCardOf topSource = new TopCardOf(MoveComponents.Source);

		// Flip a face-down card on Tableau.
		Constraint faceDown = new NotConstraint(new IsFaceUp(topSource));
		addPressMove(new FlipCardMove("FlipCard", getTableau(), faceDown));

		// Can move a column if each card in it is the same suit and descending order

		// Can move a card to an empty space or onto a card that has a higher number
		//Constraint moveDest = new OrConstraint(isEmpty, new IfConstraint (new NextRank(topDestination, bottomMoving)));
        Constraint moveDest = new IfConstraint (new NextRank(topDestination, bottomMoving));

        //AndConstraint moveColumn = new AndConstraint(descend, sameSuit);
		Constraint MC = moveColumn();
		AndConstraint pileFinish = new AndConstraint(MC, new AndConstraint(new IsAce(topMoving), new IsKing(bottomMoving))); //replace descend with finished one when it works...

        //ColumnMove tableauToTableau2 = new ColumnMove("TableauToTableau", getTableau(), new Truth(), getTableau(), moveDest);
		//SingleCardMove tableauToTableau = new SingleCardMove("TableauToTableau", getTableau(), new Truth(), getTableau(), moveDest);
		ColumnMove tableauToTableau = new ColumnMove("TableauToTableau", getTableau(), MC, getTableau(), moveDest);
		addDragMove(tableauToTableau);
        ColumnMove tableauToFoundation = new ColumnMove("TableauToFoundation", getTableau(), pileFinish, getFoundation(), isEmpty);
        addDragMove(tableauToFoundation);
        //addDragMove(tableauToTableau2);

        // deal card from stock
        NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, tableau);
        addPressMove(deckDeal);

        // When all cards are in tableau TO BE CHANGED
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Foundation, 104);
		setLogic (state);
	}
}
