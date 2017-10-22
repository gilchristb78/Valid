@(RootPackage: Name,
        MoveName: SimpleName,
        Helper: Seq[BodyDeclaration[_]],
        Do: Seq[Statement],
        Undo: Seq[Statement],
        CheckValid: Seq[Statement])
package @{Java(RootPackage)}.model;

import ks.common.model.*;
import ks.common.games.Solitaire;
import org.combinators.*;

/**
 * Move element from one stack to a number of other stacks.
 */
public class @Java(MoveName) extends ks.common.model.Move {

    /** Destination. */
    protected Stack[] destinations;

    /** Source. */
    protected Stack source;

    public @Java(MoveName) (Stack from, Stack[] to) {
        super();

        this.source = from;
        this.destinations = to;
    }

    // helper methods go here...
    // but also additional fields...
    // but also additional constructors...
    @Java(Helper)

    /**
     * Request the undo of a move.
     *
     * @@param theGame ks.games.Solitaire
     */
    public boolean undo(ks.common.games.Solitaire game) {

        // move back
        @Java(Undo)

        return true;
    }

    /**
     * Execute the move.
     *
     * @@see ks.common.model.Move#doMove(ks.games.Solitaire)
     */
    public boolean doMove(Solitaire game) {
        if (!valid (game)) {
            return false;
        }

        @Java(Do)

        return true;
    }

    /**
     * Validate the move.
     *
     * @@see ks.common.model.Move#valid(ks.games.Solitaire)
     */
    public boolean valid(Solitaire game) {

        @Java(CheckValid)
    }
}
