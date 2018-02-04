package domain.klondike;

import domain.*;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;

/**
 * differing primarily in that tableau building does not require building in alternate colors,
 * only in different suits. Also, any card or sequence can be moved into a space (rather than just kings).
 */
public class ThumbAndPouchKlondikeDomain extends KlondikeDomain {

    /** Parameterizable API. */

    /** Allow any card to be placed on empty tableau. */
    @Override
    public Constraint buildOnEmptyTableau(MoveInformation bottom) {
        return new Truth();
    }

    /** As long as SUIT is different (and lower rank) then ok. */
    @Override
    public Constraint buildOnTableau(MoveInformation bottom) {
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        return new AndConstraint(new NextRank(topDestination, bottom), new NotConstraint(new SameSuit(bottom, topDestination)));
    }

    public ThumbAndPouchKlondikeDomain() {
        super("ThumbAndPouch");
    }
}
