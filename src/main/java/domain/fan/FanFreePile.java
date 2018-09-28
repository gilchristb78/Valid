package domain.fan;

import domain.Constraint;
import domain.Reserve;
import domain.SolitaireContainerTypes;
import domain.constraints.IfConstraint;
import domain.constraints.IsEmpty;
import domain.constraints.NotConstraint;
import domain.constraints.movetypes.MoveComponents;
import domain.freeCell.FreePile;
import domain.moves.DeckDealMove;
import domain.moves.SingleCardMove;
import domain.ui.Layout;
import domain.ui.VerticalPlacement;
import domain.ui.View;
import domain.win.BoardState;

import java.awt.*;

/**
 * Programmatically construct full domain model for "Hello-World"  Fan variation
 */
public class FanFreePile extends Domain {
    private Reserve reserve;

    @Override
    public boolean hasReserve() {
        return true;
    }

    public FanFreePile() {
        super("FanFreePile");
        init();
    }

    /**
     * Reserve has 2 Free piles..
     *
     * @return
     */
    protected Reserve getReserve() {
        if (reserve == null) {
            reserve = new Reserve();
            for (int i = 0; i < 2; i++) {
                reserve.add(new FreePile());
            }
        }
        return reserve;
    }

    /**
     * Override layout as needed.
     */
    @Override
    public Layout getLayout() {
        if (layout == null) {
            super.getLayout();
            layout.add(SolitaireContainerTypes.Reserve, new VerticalPlacement(new Point(800, 250),
                    card_width, card_height, card_gap));
        }
        return layout;
    }

    private void init() {
        // we intend to be solvable
        setSolvable(true);
        registerElementAndView(new FreePile(), new View("FreePileView", "PileView", "FreePile"));
        placeContainer(getTableau());
        placeContainer(getStock());
        placeContainer(getFoundation());
        placeContainer(getReserve());
        // deal card from stock
        NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, tableau);
        addPressMove(deckDeal);

        Constraint tableauConst = new IfConstraint(new IsEmpty(MoveComponents.Destination), buildOnEmptyTableau(MoveComponents.MovingCard), buildOnTableau(MoveComponents.MovingCard));

        addDragMove(new SingleCardMove("MoveCard",getTableau(),getTableau(), tableauConst));

        Constraint toFoundation = new IfConstraint(new IsEmpty(MoveComponents.Destination), buildOnEmptyFoundation(MoveComponents.MovingCard), buildOnFoundation(MoveComponents.MovingCard));
        addDragMove(new SingleCardMove("MoveCardFoundation", getTableau(), getFoundation(), toFoundation));
        //addClickMove(new SingleCardMove("ClickMoveCardFoundation", getTableau(), getFoundation(), toFoundation));

        addDragMove(new SingleCardMove("MoveToFreePile", getTableau(), getReserve(), new IsEmpty(MoveComponents.Destination)));
        addDragMove(new SingleCardMove("MoveFromFreePile", getReserve(), getTableau(), tableauConst));

        // When all cards are in the AcesUp and KingsDown
        BoardState state = new BoardState();
        state.add(SolitaireContainerTypes.Foundation, 52);
        setLogic (state);


    }
}
