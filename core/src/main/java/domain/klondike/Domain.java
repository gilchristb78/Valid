package domain.klondike;

import domain.*;
import domain.Container;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.moves.*;
import domain.ui.HorizontalPlacement;
import domain.ui.PlacementGenerator;

import java.awt.*;
import java.util.Iterator;


/**
 * Programmatically construct full domain model for Idiot.
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
        super ("Klondike");
        PlacementGenerator places = new HorizontalPlacement(new Point(40, 200),
                card_width, 13*card_height, card_gap);

        Tableau tableau = new Tableau(places);
        tableau.add (new BuildablePile());
        tableau.add (new BuildablePile());
        tableau.add (new BuildablePile());
        tableau.add (new BuildablePile());
        tableau.add (new BuildablePile());
        tableau.add (new BuildablePile());
        tableau.add (new BuildablePile());
        containers.put(SolitaireContainerTypes.Tableau, tableau);

        places = new HorizontalPlacement(new Point(15, 20),
                card_width, card_height, card_gap);
        Stock stock = new Stock(places);
        containers.put(SolitaireContainerTypes.Stock, stock);

        places = new HorizontalPlacement(new Point(293, 20),
                card_width, card_height, card_gap);
        Foundation found = new Foundation(places);
        found.add (new Pile());
        found.add (new Pile());
        found.add (new Pile());
        found.add (new Pile());
        containers.put(SolitaireContainerTypes.Foundation, found);

        places = new HorizontalPlacement(new Point(95, 20),
                card_width, card_height, card_gap);
        Waste waste = new Waste(places);
        waste.add (new WastePile());
        containers.put(SolitaireContainerTypes.Waste, waste);

        // wins once foundation contains same number of cards as stock
        Rules rules = new Rules();

        IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);

        BottomCardOf bottomMoving = new BottomCardOf(MoveComponents.MovingColumn);
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);

        AndConstraint placeColumn = new AndConstraint(new NextRank(topDestination, bottomMoving),
                new OppositeColor(bottomMoving, topDestination));

        AndConstraint placeCard = new AndConstraint(new NextRank(topDestination, MoveComponents.MovingCard),
                new OppositeColor(MoveComponents.MovingCard, topDestination));

        // Tableau to tableau (includes to empty card)
        IfConstraint moveCol = new IfConstraint(isEmpty, new IsKing(bottomMoving), placeColumn);
        IfConstraint moveCard = new IfConstraint(isEmpty, new IsKing(MoveComponents.MovingCard), placeCard);

        // Note in Klondike, we can take advantage of fact that any face-up column of cards WILL be in Descending rank
        // and alternating colors. We could choose to be paranoid and check, but see how this is done in FreeCell.
        ColumnMove tableauToTableau = new ColumnMove("MoveColumn", tableau, new Truth(), tableau, moveCol);
        rules.addDragMove(tableauToTableau);

        SingleCardMove wasteToTableau = new SingleCardMove("MoveCard", waste, tableau, moveCard);
        rules.addDragMove(wasteToTableau);

        // Flip a face-down card on Tableau.
        NotConstraint faceDown = new NotConstraint(new IsFaceUp(new TopCardOf(MoveComponents.Source)));
        FlipCardMove tableauFlip = new FlipCardMove("FlipCard", tableau, faceDown);
        rules.addPressMove(tableauFlip);

        // Move to foundation from Waste. For Java we never checked that card at least existed; need to do so for PysolFC
        IfConstraint wf_tgt = new IfConstraint(isEmpty,
                        new AndConstraint (new IsSingle(MoveComponents.MovingCard), new IsAce(MoveComponents.MovingCard)),
                        new AndConstraint(new NextRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
                                new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination))));

        // Tableau to foundation
        IfConstraint tf_tgt = new IfConstraint(isEmpty,
                        new AndConstraint (new IsSingle(MoveComponents.MovingColumn), new IsAce(new BottomCardOf(MoveComponents.MovingColumn))),
                        new AndConstraint (new IsSingle(MoveComponents.MovingColumn),
                                new NextRank(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination)),
                                new SameSuit(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination))));

        // build on the foundation, from tableau and waste. Note that any SINGLECARD can be theoretically moved.
        ColumnMove buildFoundation = new ColumnMove("BuildFoundation",
                tableau, new IsSingle(MoveComponents.MovingColumn),
                found,   tf_tgt);
        rules.addDragMove(buildFoundation);

        SingleCardMove buildFoundationFromWaste = new SingleCardMove("BuildFoundationFromWaste",
                waste,   new Truth(),
                found,   wf_tgt);
        rules.addDragMove(buildFoundationFromWaste);

        // Deal card from deck
        NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, waste, new Truth());
        rules.addPressMove(deckDeal);

        // reset deck if empty. Move is triggered by press on stock.
        // this creates DeckToPile, as in the above DeckDealMove.
        ResetDeckMove deckReset = new ResetDeckMove("ResetDeck", stock, new IsEmpty(MoveComponents.Source), waste, new Truth());
        rules.addPressMove(deckReset);

        setRules(rules);


        Deal d = new Deal();

        // each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
        // don't forget zero-based indexing.
        for (int pileNum = 1; pileNum < 7; pileNum++) {
            Payload payload = new Payload(pileNum, false);
            DealStep step = new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, tableau, pileNum), payload);
            d.add(step);
        }

        // finally each one gets a single faceup Card
        Payload payload = new Payload();
        DealStep step = new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau, tableau), payload);
        d.add(step);

        // deal one card to waste pile
        step = new DealStep(new ContainerTarget(SolitaireContainerTypes.Waste, waste), new Payload());
        d.add(step);
        setDeal(d);
    }
}