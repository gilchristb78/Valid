package domain.klondike;

import domain.Constraint;
import domain.SolitaireContainerTypes;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.DealToWaste;

/**
 * 2 face down cards dealt to each of six columns in Tableau, with one face up
 * Any card to empty space. No redeal.
 *
 */
public class Whitehead extends KlondikeDomain {

    private Deal deal;

    /** Never allow redeals. */
    @Override
    public int numRedeals() {
        return NEVER_REDEAL;
    }

    /** Allow any card to be placed on empty tableau. */
    @Override
    public Constraint buildOnEmptyTableau(MoveInformation bottom) {
        return new Truth();
    }

    /** As long as SUIT is same (and lower rank) then ok. */
    @Override
    public Constraint buildOnTableau(MoveInformation bottom) {
        TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
        return new AndConstraint(new NextRank(topDestination, bottom), new SameSuit(bottom, topDestination));
    }

    /** Override deal as needed. */
    @Override
    public Deal getDeal() {
        if (deal == null) {
            deal = new Deal();

            // all cards are face up
            for (int pileNum = 0; pileNum < 7; pileNum++) {
                deal.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, pileNum), new Payload(pileNum+1, true)));
            }

            // finally each one gets a single faceup Card, and deal one to waste pile
            //add(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), new Payload()));
            deal.append(new DealToWaste(numToDeal()));
        }

        return deal;
    }

    public Whitehead() {
        super("Whitehead");

        // remove the redeal option
        remove(getRules().presses(), "ResetDeck");
    }
}
