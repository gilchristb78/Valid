@(RootPackage: Name, MoveName: SimpleName, DraggingCardVariableName: SimpleName, Type: SimpleName)
package @{Java(RootPackage)}.model;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Potential Move when multiple cards in play; note that 'numCards' is inherited, and is
 * drawn from the MoveHelper combinators that created the parent Move classes in the first place.
 *
 * Parameters:
 *    RootPackage
 *    Designate
 *    DraggingCard
 */
public class Potential@{Java(MoveName)} extends @{Java(MoveName)} {

    /** Destination. */
    public Potential@{Java(MoveName)} (Stack from, Stack to) {
        super(from, to);
        numCards = 1;   // was numInColumn
    }

    public Potential@{Java(MoveName)} (Stack from, Stack to, int num) {
        super(from, to);
        numCards = num;
    }

    @@Override
    public boolean valid(Solitaire game) {
        if (@Java(DraggingCardVariableName) == null) {
            if (source.count() < numCards) { return false; }

            // make sure to keep order of potential column intact
            synchronized (this) {
                @Java(DraggingCardVariableName) = new @{Java(Type)}();
                source.select(numCards);  // numInColumn
                @{Java(DraggingCardVariableName)}.push(source.getSelected());
                boolean result = super.valid(game);
                source.push( @Java(DraggingCardVariableName) );

                return result;
            }
        } else {
            return super.valid(game);
        }
    }

    @@Override
    public boolean doMove(Solitaire game) {
        if (!valid(game)) { return false; }

        synchronized (this) {
            @Java(DraggingCardVariableName) = new @{Java(Type)}();
            source.select(numCards);  // numInColumn
            @{Java(DraggingCardVariableName)}.push(source.getSelected());
            boolean result = super.doMove(game);

            return result;
        }
    }
}