package domain.fan;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.Deal;
import domain.deal.*;
import domain.moves.DeckDealMove;
import domain.moves.SingleCardMove;
import domain.ui.CalculatedPlacement;
import domain.ui.HorizontalPlacement;
import domain.deal.steps.DealToTableau;
import domain.ui.Layout;
import domain.win.BoardState;
import java.awt.*;

/**
 * Programmatically construct full domain model for "Hello-World"  Fan variation
 */
public class Domain extends Solitaire implements VariationPoints {

	protected Deal deal;
	protected Layout layout;
	protected Tableau tableau;
	protected Stock stock;
	protected Foundation foundation;

	public boolean hasReserve(){
		return false;
	}

	public Tableau getTableau() {
		if (tableau == null) {
			tableau = new Tableau();
			for (int i = 0; i< 18; i++) {
				tableau.add(new Column());
			}
		}
		return tableau;
	}
	public Foundation getFoundation(){
		if (foundation == null) {
			foundation = new Foundation();

			for (int i = 0; i < 4; i++) {
				foundation.add(new Pile());
			}
		}
		return foundation;
	}

	/** Override deal as needed. Nothing dealt. */
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal();
			deal.append(new DealToTableau(2));
			//only first 16 cols get a third
			for (int colNum = 0; colNum < 16; colNum++) {
				deal.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, colNum), new Payload(1, true)));
			}

		}
		return deal;
	}

	/** Override layout as needed. */
	@Override
	public Layout getLayout() {
		Point [] anchors = new Point[30];
		int j = 0;
		for (int i =0; i <18; i++){
			if (i%6 == 0){
				j++;
			}
				anchors[i] = new Point(100+(i%6)*(card_gap*2 + card_width), 200*j);
		}
		if (layout == null) {
			layout = new Layout()
					.add(SolitaireContainerTypes.Tableau, new CalculatedPlacement(anchors,
							card_width, card_height*2))
					.add(SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point( 200, 10),
							card_width, card_height, card_gap));
		}
		return layout;
	}

	public Stock getStock() {
		if (stock == null) {
			// Fan has a single deck
			stock = new Stock(1);
		}
		return stock;
	}

	public Domain() {
		super ("Fan");
		init();
	}

	public Domain(String s) {
		super (s);
	}

	/**
	 * Determines what cards can be placed on tableau.
	 *
	 * Parameter is either a single card
	 *
	 * @param bottom
	 * @return
	 */
	public Constraint buildOnTableau(MoveInformation bottom) {
		TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
		return new AndConstraint(new NextRank(topDestination, bottom), new SameSuit(bottom, topDestination));
	}

	/**
	 * Determines what cards can be placed on tableau.
	 *
	 * Parameter is either a single card, or something like BottomOf().
	 *
	 * @param bottom
	 * @return
	 */
	public Constraint buildOnFoundation(MoveInformation bottom) {
		TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
		return new AndConstraint(new NextRank(bottom, topDestination), new SameSuit(bottom, topDestination));
	}


	/**
	 * By default, only Kings can be placed in empty tableau.
	 * @param bottom
	 * @return
	 */
	public Constraint buildOnEmptyTableau(MoveInformation bottom) {
		return new IsKing(bottom);
	}

	/**
	 * By default, only Aces can be placed in empty Foundation
	 * @param bottom
	 * @return
	 */
	public Constraint buildOnEmptyFoundation(MoveInformation bottom) {
		return new IsAce(bottom);
	}

	public void init() {
		// we intend to be solvable
		setSolvable(true);

		placeContainer(getTableau());
		placeContainer(getStock());
		placeContainer(getFoundation());
		// deal card from stock
		NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
		DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, tableau);
		addPressMove(deckDeal);

		Constraint tableauConst = new IfConstraint(new IsEmpty(MoveComponents.Destination), buildOnEmptyTableau(MoveComponents.MovingCard), buildOnTableau(MoveComponents.MovingCard));

		addDragMove(new SingleCardMove("MoveCard",getTableau(),getTableau(), tableauConst));

		Constraint toFoundation = new IfConstraint(new IsEmpty(MoveComponents.Destination), buildOnEmptyFoundation(MoveComponents.MovingCard), buildOnFoundation(MoveComponents.MovingCard));
		addDragMove(new SingleCardMove("MoveCardFoundation", getTableau(), getFoundation(), toFoundation));
		//addClickMove(new SingleCardMove("ClickMoveCardFoundation", getTableau(), getFoundation(), toFoundation));
		// When all cards are in the AcesUp and KingsDown
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Foundation, 52);
		setLogic (state);
	}
}
