package domain.bigforty;
import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.*;
import domain.deal.*;
import domain.moves.*;
import domain.ui.HorizontalPlacement;
import domain.ui.PlacementGenerator;
import domain.win.BoardState;

import java.awt.*;
import java.util.Iterator;

public class Domain extends Solitaire {

    public static void main (String[] args) {
        Domain sfc = new Domain();
        System.out.println("Available Moves:");
        for (Iterator<Move> it = sfc.getRules().drags(); it.hasNext(); ) {
            System.out.println("  " + it.next());

        }
    }

    public Domain() {
        super ("BigForty");
        PlacementGenerator places = new HorizontalPlacement(new Point(15, 200),
                card_width, 13*card_height, card_gap);

        Tableau tableau = new Tableau();
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        placeContainer(tableau, places);

        containers.put(SolitaireContainerTypes.Tableau, tableau);

        places = new HorizontalPlacement(new Point(15, 20),
                card_width, card_height, card_gap);
        Stock stock = new Stock();
        placeContainer(stock, places);
        containers.put(SolitaireContainerTypes.Stock, stock);

        places = new HorizontalPlacement(new Point(293, 20),
                card_width, card_height, card_gap);
        Foundation found = new Foundation();
        placeContainer(found, places);

        found.add (new Pile());
        found.add (new Pile());
        found.add (new Pile());
        found.add (new Pile());
        containers.put(SolitaireContainerTypes.Foundation, found);

        places = new HorizontalPlacement(new Point(95, 20),
                card_width, card_height, card_gap);
        Waste waste = new Waste();
        placeContainer(waste, places);
        waste.add (new WastePile());
        containers.put(SolitaireContainerTypes.Waste, waste);

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

        ColumnMove tableauToTableau = new ColumnMove("MoveColumn", tableau, and_2, tableau, or);
        addDragMove(tableauToTableau);

        //2. waste to tableau
        OrConstraint moveCard= new OrConstraint(isEmpty,new NextRank(new TopCardOf(MoveComponents.Destination),
                MoveComponents.MovingCard));
        SingleCardMove wasteToTableau = new SingleCardMove("MoveCard", waste, tableau, moveCard);
        addDragMove(wasteToTableau);

        //3. waste to foundation  4.tableau to foundation
        IsSingle isSingle = new IsSingle(MoveComponents.MovingColumn);
        IfConstraint tf_tgt = new IfConstraint(isEmpty,
                new AndConstraint (new IsSingle(MoveComponents.MovingColumn), new IsAce(new BottomCardOf(MoveComponents.MovingColumn))),
                new AndConstraint (new IsSingle(MoveComponents.MovingColumn),
                        new NextRank(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination)),
                        new SameSuit(new BottomCardOf(MoveComponents.MovingColumn), new TopCardOf(MoveComponents.Destination))));

        ColumnMove buildFoundation = new ColumnMove("BuildFoundation",
                tableau, new IsSingle(MoveComponents.MovingColumn),
                found,   tf_tgt);
        addDragMove(buildFoundation);
        IfConstraint wf_tgt = new IfConstraint(isEmpty,
                new AndConstraint (new IsSingle(MoveComponents.MovingCard), new IsAce(MoveComponents.MovingCard)),
                new AndConstraint(new NextRank(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination)),
                        new SameSuit(MoveComponents.MovingCard, new TopCardOf(MoveComponents.Destination))));

        SingleCardMove buildFoundationFromWaste = new SingleCardMove("BuildFoundationFromWaste",
                waste,   new Truth(),
                found,   wf_tgt);
        addDragMove(buildFoundationFromWaste);

        // Deal card from deck
        NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
        DeckDealMove deckDeal = new DeckDealMove("DealDeck", stock, deck_move, waste);
        addPressMove(deckDeal);

        // reset deck if empty. Move is triggered by press on stock.
        // this creates DeckToPile, as in the above DeckDealMove.
        ResetDeckMove deckReset = new ResetDeckMove("ResetDeck", stock, new IsEmpty(MoveComponents.Source), waste);
        addPressMove(deckReset);

        // Deal Logic
        addDealStep (new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau, tableau), new Payload(4, true)));

        // deal one card to waste pile
        addDealStep (new DealStep(new ContainerTarget(SolitaireContainerTypes.Waste, waste), new Payload()));

        // wins once all cards in foundation.
        BoardState state = new BoardState();
        state.add(SolitaireContainerTypes.Foundation, 52);
        setLogic (state);
    }
}
