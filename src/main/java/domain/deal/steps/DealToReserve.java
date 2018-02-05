package domain.deal.steps;

import domain.SolitaireContainerTypes;
import domain.deal.ContainerTarget;
import domain.deal.Deal;
import domain.deal.DealStep;

public class DealToReserve extends Deal {

    /** Deal 1 face-up card to each of the Reserve elements. */
    public DealToReserve() {
        append(new DealStep(new ContainerTarget(SolitaireContainerTypes.Reserve)));

    }
}
