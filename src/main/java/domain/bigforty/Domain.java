package domain.bigforty;
import domain.*;
import domain.Container;
import domain.constraints.*;
import domain.constraints.movetypes.*;
import domain.deal.*;
import domain.deal.steps.DealToFoundation;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.DealToWaste;
import domain.deal.steps.FilterAces;
import domain.moves.*;
import domain.ui.HorizontalPlacement;
import domain.ui.Layout;
import domain.ui.PlacementGenerator;
import domain.ui.View;
import domain.win.BoardState;

import java.awt.*;
import java.util.Iterator;

public class Domain extends Solitaire {

    private Deal deal;
    private Layout layout;
    private Tableau tableau;
    private Stock stock;
    private Foundation foundation;
    private Waste waste;

    /** Override deal as needed. */
    @Override
    public Deal getDeal() {
        if (deal == null) {
            deal = new Deal()
                    .append(new DealToTableau(4))
                    .append(new DealToWaste());
        }

        return deal;
    }

    /** Override layout as needed. */
    @Override
    public Layout getLayout() {
        if (layout == null) {
            layout = new Layout()
                    .add(SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point(15, 200),
                            card_width, 13*card_height, card_gap))
                    .add(SolitaireContainerTypes.Stock, new HorizontalPlacement(new Point(15, 20),
                            card_width, card_height, card_gap))
                    .add(SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(293, 20),
                            card_width, card_height, card_gap))
                    .add(SolitaireContainerTypes.Waste, new HorizontalPlacement(new Point(95, 20),
                            card_width, card_height, card_gap));

        }

        return layout;
    }


    public Tableau getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 10; i++) {
                tableau.add(new Column());
            }
        }

        return tableau;
    }

    /**
     * Default Foundation has four piles..
     *
     * @return
     */
    protected Foundation getFoundation() {
        if (foundation == null) {
            foundation = new Foundation();
            for (int i = 0; i < 4; i++) { foundation.add (new Pile()); }
        }
        return foundation;
    }

    /**
     * Default Waste has a single WastePile
     *
     * @return
     */
    protected Waste getWaste() {
        if (waste == null) {
            waste = new Waste();
            waste.add (new WastePile());
        }
        return waste;
    }

    /**
     * A single Stock of a single deck of cards.
     *
     * @return
     */
    protected Stock getStock() {
        if (stock == null) { stock = new Stock(); }
        return stock;
    }

    public Domain() {
        super ("BigForty");
        init();
    }

    // register new elements for this domain
    @Override
    public void registerElements() {
        registerElementAndView(new WastePile(), new View("WastePileView", "PileView", "WastePile"));
    }

    private void init() {
        placeContainer(getTableau());
        placeContainer(getStock());
        placeContainer(getFoundation());
        placeContainer(getWaste());

        IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);
        NextRank nextOne =  new NextRank(new TopCardOf(MoveComponents.Destination), MoveComponents.MovingCard);

        BottomCardOf bottomMoving = new BottomCardOf(MoveComponents.MovingColumn);
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);

        //1.tableau to tableau

        //constraint to the destination
        AndConstraint and= new AndConstraint(new NextRank(new TopCardOf(MoveComponents.Destination),
                new BottomCardOf(MoveComponents.MovingColumn)),new SameSuit(new TopCardOf(MoveComponents.Destination),
                new BottomCardOf(MoveComponents.MovingColumn)));
        OrConstraint or = new OrConstraint(isEmpty, and);

        //constraint to the source
        Descending descend = new Descending(MoveComponents.MovingColumn);
        AndConstraint and_2= new AndConstraint(descend,new AllSameSuit(MoveComponents.MovingColumn));

        ColumnMove tableauToTableau = new ColumnMove("MoveColumn",  getTableau(), and_2,  getTableau(), or);
        addDragMove(tableauToTableau);

        //2. waste to tableau
        OrConstraint moveCard= new OrConstraint(isEmpty,new NextRank(new TopCardOf(MoveComponents.Destination),
                MoveComponents.MovingCard));
        SingleCardMove wasteToTableau = new SingleCardMove("MoveCard", getWaste(), getTableau(), moveCard);
        addDragMove(wasteToTableau);

        //3. waste to foundation  4.tableau to foundation
        IsSingle isSingle = new IsSingle(MoveComponents.MovingColumn);
        IfConstraint tf_tgt = new IfConstraint(isEmpty,
                new AndConstraint (new IsSingle(MoveComponents.MovingColumn), new IsAce(new BottomCardOf(MoveComponents.MovingColumn))),
                new AndConstraint (new IsSingle(MoveComponents.MovingColumn),
                        new NextRank(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination)),
                        new SameSuit(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination))));

        ColumnMove buildFoundation = new ColumnMove("BuildFoundation",
                getTableau(),      new IsSingle(MoveComponents.MovingColumn),
                getFoundation(),   tf_tgt);
        addDragMove(buildFoundation);
        IfConstraint wf_tgt = new IfConstraint(isEmpty,
                new AndConstraint (new IsSingle(MoveComponents.MovingCard), new IsAce(MoveComponents.MovingCard)),
                new AndConstraint(new NextRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
                        new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination))));

        SingleCardMove buildFoundationFromWaste = new SingleCardMove("BuildFoundationFromWaste",
                getWaste(),        new Truth(),
                getFoundation(),   wf_tgt);
        addDragMove(buildFoundationFromWaste);

        // Deal card from deck
        NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        DeckDealMove deckDeal = new DeckDealMove("DealDeck", getStock(), deck_move, getWaste());
        addPressMove(deckDeal);

        // reset deck if empty. Move is triggered by press on stock.
        // this creates DeckToPile, as in the above DeckDealMove.
        ResetDeckMove deckReset = new ResetDeckMove("ResetDeck", getStock(), new IsEmpty(MoveComponents.Source), getWaste());
        addPressMove(deckReset);

        // wins once all cards in foundation.
        BoardState state = new BoardState();
        state.add(SolitaireContainerTypes.Foundation, 52);
        setLogic (state);
    }
}
