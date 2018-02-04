package domain.klondike;

import domain.SolitaireContainerTypes;
import domain.deal.*;

/**
 * 2 face down cards dealt to each of six columns in Tableau, with one face up
 * Any card to empty space. No redeal.
 *
 */
public class EastCliff extends KlondikeDomain {

    private Deal deal;

    @Override
    public int numRedeals() {
        return NEVER_REDEAL;
    }

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

    public EastCliff() {
        super("EastCliff");
    }

    // for sub-variations
    public EastCliff(String name) {
        super(name);
    }
}
