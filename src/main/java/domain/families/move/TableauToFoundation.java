package domain.families.move;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.moves.ColumnMove;

import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Reflects a move from tableau to the foundation.
 */
public class TableauToFoundation {

    public void sample() {

        // function to be used with 'andThen' -- perhaps could be used as  a way to programmatically
        // build up the solitaire structure.
        Function<Solitaire,Solitaire> f = x-> x;


        //BiFunction<Integer, Integer, String
        /**

         Solitaire s = nwe Solitaire()



         */
    }

    public Move move(Solitaire sol) {
        Container tableau = sol.structure.get(SolitaireContainerTypes.Tableau);

        // Rules of Klondike defined below
        IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);
        BottomCardOf bottomMoving = new BottomCardOf(MoveComponents.MovingColumn);
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);

        Constraint placeColumn = new AndConstraint(new NextRank(topDestination, bottomMoving),
                new OppositeColor(bottomMoving, topDestination));

        // Tableau to tableau (includes to empty card)
        Constraint moveCol = new IfConstraint(isEmpty, new IsKing(bottomMoving), placeColumn);

        // Note in Klondike, we can take advantage of fact that any face-up column of cards WILL be in Descending rank
        // and alternating colors. We could choose to be paranoid and check, but see how this is done in FreeCell.
        return new ColumnMove("MoveColumn", tableau, new Truth(), tableau, moveCol);

        /*

        return new ColumnMove("MoveColumn", tableau, tableau)
            .restrictTarget(moveCol)



         */
    }
}
