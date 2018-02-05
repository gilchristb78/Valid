package domain.klondike;

import domain.SolitaireContainerTypes;
import domain.deal.ContainerTarget;
import domain.deal.Deal;
import domain.deal.DealStep;
import domain.deal.Payload;

/**
 * Like EastCliff, but only one redeal is allowed.
 *
 * Recording this in the domain is quite easy. Surprisingly, the back-end requirement
 * to make this work in KombatSolitaire may seem more complicated than it should be,
 * but after all, this is adding state to a Move Class, which does make it more complicated.
 * Initial idea is to have ResetDeck move have a static attribute, which records the
 * number of resets allowed; decrement each time, and then disallow after zero.
 * ConstraintHelper can be modified to inspect LOCAL static attribute
 */
public class EastHaven extends EastCliff {

    private Deal deal;

    /** One redeal supported. */
    @Override
    public int numRedeals() { return 1; }

    /** Override deal as needed. */
    @Override
    public Deal getDeal() {
        if (deal == null) {
            deal = new Deal()
                    .append(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), new Payload(2, false)))
                    .append(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), new Payload(1, true)));
        }

        return deal;
    }

    public EastHaven() {
        super("EastHaven");
    }

    // for sub-variations
    public EastHaven(String name) {
        super(name);
    }
}
