package domain.fan;

import domain.Constraint;
import domain.constraints.AndConstraint;
import domain.constraints.MoveInformation;
import domain.constraints.NextRank;
import domain.constraints.OppositeColor;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;

public class ScotchPatience extends Domain{

    public ScotchPatience(){
        super("ScotchPatience");
        init();
    }
    /**
     * Determines what cards can be placed on tableau.
     *
     * Parameter is either a single card
     *
     * @param bottom
     * @return
     */
    @Override
    public Constraint buildOnTableau(MoveInformation bottom) {
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        return new NextRank(topDestination, bottom);
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
    public Constraint buildOnFoundation(MoveInformation bottom) {
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        return new AndConstraint(new NextRank(bottom, topDestination), new OppositeColor(bottom, topDestination));
    }

}