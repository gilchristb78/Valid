package domain.klondike;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
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

        //    val semanticType: Type =
//        'Waste ('Valid :&: 'One :&: 'Pile) =>:
//  }


//      lay.add(Layout.WastePile, 95, 20, 73, 97)

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

        ColumnMove tableauToTableau = new ColumnMove("MoveColumn", tableau, tableau, moveCol);
        rules.addDragMove(tableauToTableau);

        SingleCardMove wasteToTableau = new SingleCardMove("MoveCard", waste, tableau, moveCard);
        rules.addDragMove(wasteToTableau);

        // Flip a face-down card on Tableau.
        NotConstraint faceDown = new NotConstraint(new IsFaceUp(new TopCardOf(MoveComponents.Source)));
        FlipCardMove tableauFlip = new FlipCardMove("FlipCard", tableau, faceDown);
        rules.addPressMove(tableauFlip);

        // Move to foundation from Tableau
        IfConstraint if3 = new IfConstraint(isEmpty,
                        new IsAce(MoveComponents.MovingCard),
                        new AndConstraint(new NextRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
                                new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination))));

        IfConstraint if2 = new IfConstraint(isEmpty,
                        new IsAce(new TopCardOf(MoveComponents.MovingColumn)),
                        new AndConstraint (new NotConstraint(new IsSingle(MoveComponents.MovingColumn)),
                                new NextRank(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination)),
                                new SameSuit(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination))));

        // build on the foundation, from tableau and waste
        ColumnMove buildFoundation = new ColumnMove("BuildFoundation", tableau, found, if2);
        rules.addDragMove(buildFoundation);
        SingleCardMove buildFoundationFromWaste = new SingleCardMove("BuildFoundationFromWaste", waste, found, if3);
        rules.addDragMove(buildFoundationFromWaste);

        // Deal card from deck
        NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, waste, deck_move);
        rules.addPressMove(deckDeal);

        // reset deck if empty. Move is triggered by press on stock.
        // this creates DeckToPile, as in the above DeckDealMove.
        ResetDeckMove deckReset = new ResetDeckMove("ResetDeck", stock, waste, new IsEmpty(MoveComponents.Source));
        rules.addPressMove(deckReset);

        setRules(rules);
    }
}
