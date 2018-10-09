package domain.fan;

import domain.Constraint;
import domain.SolitaireContainerTypes;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.steps.DealToTableau;

public class Shamrocks extends Domain {

    public Shamrocks (){
        super ("Shamrocks");
        init();
    }
 /**
     * Determines what cards can be placed on tableau.
     *
     * Parameter is either a single card
     *
     * @param bottom
     * @return
     *
  * */
    @Override
    public Constraint buildOnTableau(MoveInformation bottom) {
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        return new AndConstraint(new OrConstraint(new NextRank(topDestination, bottom),new NextRank(bottom,topDestination)), new MaxSizeConstraint(bottom, MoveComponents.Destination, 3) );
    }

    /** Override deal as needed. Nothing dealt. */
    @Override
    public Deal getDeal() {
        if (deal == null) {
            deal = new Deal();
            deal.append(new FilterStep(new NotConstraint(new IsKing(DealComponents.Card))));
           //By spliting this up is assures king is on bottom as first 4 cards are kings
            deal.append(new DealToTableau(1));
            deal.append(new DealToTableau(1));
            //only first 16 cols get a third
            for (int colNum = 0; colNum < 16; colNum++) {
                deal.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, colNum), new Payload(1, true)));

            }


        }
        return deal;
    }

}
