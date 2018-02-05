package domain.klondike;

import domain.*;
import domain.Container;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.DealToWaste;
import domain.moves.*;
import domain.ui.Layout;
import domain.ui.View;
import domain.win.BoardState;


/** Programmatically construct full domain model for Klondike. */
public class KlondikeDomain extends Solitaire implements VariationPoints {

    /** Parameterizable API. */
    @Override
    public int numToDeal() {
        return 1;
    }

    /** Any number of redeals possible. */
    @Override
    public int numRedeals() {
        return INFINITE_REDEAL;
    }

    /**
     * Determines what cards can be placed on tableau.
     *
     * Parameter is either a single card, or something like BottomOf().
     *
     * @param bottom
     * @return
     */
    @Override
    public Constraint buildOnTableau(MoveInformation bottom) {
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        return new AndConstraint(new NextRank(topDestination, bottom), new OppositeColor(bottom, topDestination));
    }

    /**
     * By default, only Kings can be placed in empty tableau.
     * @param bottom
     * @return
     */
    @Override
    public Constraint buildOnEmptyTableau(MoveInformation bottom) {
        return new IsKing(bottom);
    }

    /** Structure. */
    private Tableau tableau;
    private Stock stock;
    private Foundation foundation;
    private Waste waste;
    private Layout layout;
    private Deal deal;

    /**
     * Default Klondike Tableau has seven buildable piles.
     *
     * @return
     */
    protected Container getTableau() {
        if (tableau == null) {
            tableau = new Tableau();
            for (int i = 0; i < 7; i++) { tableau.add (new BuildablePile()); }
        }
        return tableau;
    }

    protected Container getWaste() {
        if (waste == null) {
            waste = new Waste();
            waste.add (new WastePile());
        }

        return waste;
    }

    /**
     * Default Klondike Foundation has four Foundation piles.
     *
     * @return
     */
    protected Container getFoundation() {
        if (foundation == null) {
            foundation = new Foundation();
            for (int i = 0; i < 4; i++) { foundation.add(new Pile()); }
        }

        return foundation;
    }

    /**
     * Default Klondike has a single Stock of a single deck of cards.
     *
     * @return
     */
    protected Container getStock() {
        if (stock == null) { stock = new Stock(); }
        return stock;
    }

    /** Override layout as needed. */
    @Override
    public Layout getLayout() {
        if (layout == null) {
            layout = new KlondikeLayout();
        }

        return layout;
    }

    /** Override deal as needed. */
    @Override
    public Deal getDeal() {
        if (deal == null) {
            deal = new Deal();

            // each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
            // don't forget zero-based indexing.
            for (int pileNum = 1; pileNum < 7; pileNum++) {
                deal.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, pileNum), new Payload(pileNum, false)));
            }

            // finally each one gets a single faceup Card, and deal one to waste pile
            //add(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), new Payload()));
            deal.append(new DealToTableau());
            deal.append(new DealToWaste(numToDeal()));
        }

        return deal;
    }

    public KlondikeDomain() {
        this("Klondike");
    }

    /** Only here for pass-through to subclasses. */
    protected KlondikeDomain(String name) {
        super(name);
        init();
    }

    // register new elements for this domain
    @Override
    public void registerElements() {
        registerElementAndView(new WastePile(), new View("WastePileView", "PileView", "WastePile"));
    }

    private void init() {
        // Add each container to structure
        placeContainer(getTableau());
        placeContainer(getStock());
        placeContainer(getFoundation());
        placeContainer(getWaste());

        // Rules of Klondike defined below
        IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);
        BottomCardOf bottomMoving = new BottomCardOf(MoveComponents.MovingColumn);
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        TopCardOf topSource = new TopCardOf(MoveComponents.Source);

        //Constraint placeColumn = new AndConstraint(new NextRank(topDestination, bottomMoving),
        //        new OppositeColor(bottomMoving, topDestination));

        //Constraint placeCard = new AndConstraint(new NextRank(topDestination, MoveComponents.MovingCard),
        //        new OppositeColor(MoveComponents.MovingCard, topDestination));

        // Tableau to tableau (includes to empty card)
        // Constraint moveCol = new IfConstraint(isEmpty, new IsKing(bottomMoving), placeColumn);
        //Constraint moveCard = new IfConstraint(isEmpty, new IsKing(MoveComponents.MovingCard), placeCard);

        Constraint buildCol = new IfConstraint(isEmpty, buildOnEmptyTableau(bottomMoving), buildOnTableau(bottomMoving));
        Constraint buildCard = new IfConstraint(isEmpty, buildOnEmptyTableau(MoveComponents.MovingCard), buildOnTableau(MoveComponents.MovingCard));

        // Note in Klondike, we can take advantage of fact that any face-up column of cards WILL be in Descending rank
        // and alternating colors. We could choose to be paranoid and check, but see how this is done in FreeCell.
        addDragMove(new ColumnMove("MoveColumn", getTableau(), new Truth(), getTableau(), buildCol));
        addDragMove(new SingleCardMove("MoveCard", getWaste(), getTableau(), buildCard));

        // Flip a face-down card on Tableau.
        Constraint faceDown = new NotConstraint(new IsFaceUp(topSource));
        addPressMove(new FlipCardMove("FlipCard", getTableau(), faceDown));

        // Move to foundation from Waste. For Java we never checked that card at least existed; need to do so for PysolFC
        Constraint wf_tgt = new IfConstraint(isEmpty,
                        new AndConstraint (new IsSingle(MoveComponents.MovingCard), new IsAce(MoveComponents.MovingCard)),
                        new AndConstraint(new NextRank(MoveComponents.MovingCard, topDestination),
                                new SameSuit(MoveComponents.MovingCard, topDestination)));

        // Tableau to foundation
        Constraint tf_tgt = new IfConstraint(isEmpty,
                        new AndConstraint (new IsSingle(MoveComponents.MovingColumn), new IsAce(bottomMoving)),
                        new AndConstraint (new IsSingle(MoveComponents.MovingColumn),
                                new NextRank(bottomMoving, topDestination),
                                new SameSuit(bottomMoving, topDestination)));

        // build on the foundation, from tableau and waste. Note that any SINGLECARD can be theoretically moved.
        addDragMove (new ColumnMove("BuildFoundation",
                getTableau(), new IsSingle(MoveComponents.MovingColumn), getFoundation(), tf_tgt));
        addDragMove (new SingleCardMove("BuildFoundationFromWaste", getWaste(), new Truth(), getFoundation(), wf_tgt));

        // Deal card from deck
        Constraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));

        // already take advantage of the parameter for this domain.
        addPressMove(new DeckDealNCardsMove(numToDeal(),"DealDeck", getStock(), deck_move, getWaste()));

        // reset deck if empty. Move is triggered by press on stock.
        // this creates DeckToPile, as in the above DeckDealMove. Only allow if redeals allowed; this also
        // takes care of limited redeals, thanks to the synthesized code.
        if (numRedeals() != 0) {
            addPressMove(new ResetDeckMove("ResetDeck", getStock(),
                    new AndConstraint(new RedealsAllowed(), new IsEmpty(MoveComponents.Source)), getWaste()));
        }

        // wins once all cards in foundation.
        BoardState state = new BoardState();
        state.add(SolitaireContainerTypes.Foundation, 52);
        setLogic (state);
    }
}