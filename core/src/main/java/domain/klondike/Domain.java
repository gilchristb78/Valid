package domain.klondike;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.moves.*;
import domain.ui.HorizontalPlacement;
import domain.ui.PlacementGenerator;

import java.awt.*;


/** Programmatically construct full domain model for Klondike. */
public class Domain extends Solitaire {

    public Domain() {
        super ("Klondike");
        PlacementGenerator places = new HorizontalPlacement(new Point(40, 200),
                card_width, 13*card_height, card_gap);

        Tableau tableau = new Tableau(places);
        for (int i = 0; i < 7; i++) { tableau.add (new BuildablePile()); }
        containers.put(SolitaireContainerTypes.Tableau, tableau);

        places = new HorizontalPlacement(new Point(15, 20),
                card_width, card_height, card_gap);
        Stock stock = new Stock(places);
        containers.put(SolitaireContainerTypes.Stock, stock);

        places = new HorizontalPlacement(new Point(293, 20),
                card_width, card_height, card_gap);
        Foundation found = new Foundation(places);
        for (int i = 0; i < 4; i++) { found.add (new Pile()); }
        containers.put(SolitaireContainerTypes.Foundation, found);

        places = new HorizontalPlacement(new Point(95, 20),
                card_width, card_height, card_gap);
        Waste waste = new Waste(places);
        waste.add (new WastePile());
        containers.put(SolitaireContainerTypes.Waste, waste);

        // Rules of Klondike defined below
        IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);
        BottomCardOf bottomMoving = new BottomCardOf(MoveComponents.MovingColumn);
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        TopCardOf topSource = new TopCardOf(MoveComponents.Source);

        Constraint placeColumn = new AndConstraint(new NextRank(topDestination, bottomMoving),
                new OppositeColor(bottomMoving, topDestination));

        Constraint placeCard = new AndConstraint(new NextRank(topDestination, MoveComponents.MovingCard),
                new OppositeColor(MoveComponents.MovingCard, topDestination));

        // Tableau to tableau (includes to empty card)
        Constraint moveCol = new IfConstraint(isEmpty, new IsKing(bottomMoving), placeColumn);
        Constraint moveCard = new IfConstraint(isEmpty, new IsKing(MoveComponents.MovingCard), placeCard);

        // Note in Klondike, we can take advantage of fact that any face-up column of cards WILL be in Descending rank
        // and alternating colors. We could choose to be paranoid and check, but see how this is done in FreeCell.
        addDragMove(new ColumnMove("MoveColumn", tableau, new Truth(), tableau, moveCol));
        addDragMove(new SingleCardMove("MoveCard", waste, tableau, moveCard));

        // Flip a face-down card on Tableau.
        Constraint faceDown = new NotConstraint(new IsFaceUp(topSource));
        addPressMove(new FlipCardMove("FlipCard", tableau, faceDown));

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
                tableau, new IsSingle(MoveComponents.MovingColumn), found, tf_tgt));
        addDragMove (new SingleCardMove("BuildFoundationFromWaste", waste, new Truth(), found, wf_tgt));

        // Deal card from deck
        Constraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        addPressMove(new DeckDealMove("DealDeck", stock, deck_move, waste, new Truth()));

        // reset deck if empty. Move is triggered by press on stock.
        // this creates DeckToPile, as in the above DeckDealMove.
        addPressMove (new ResetDeckMove("ResetDeck", stock, new IsEmpty(MoveComponents.Source), waste, new Truth()));

        // each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
        // don't forget zero-based indexing.
        for (int pileNum = 1; pileNum < 7; pileNum++) {
            addDealStep(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, tableau, pileNum), new Payload(pileNum, false)));
        }

        // finally each one gets a single faceup Card, and deal one to waste pile
        addDealStep(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau, tableau), new Payload()));
        addDealStep(new DealStep(new ContainerTarget(SolitaireContainerTypes.Waste, waste), new Payload()));
    }
}