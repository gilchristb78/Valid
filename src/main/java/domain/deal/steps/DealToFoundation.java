package domain.deal.steps;

import domain.SolitaireContainerTypes;
import domain.deal.ContainerTarget;
import domain.deal.Deal;
import domain.deal.DealStep;
import domain.deal.Payload;

public class DealToFoundation extends Deal {

    /** Deal 1 face-up card to each of the Foundation elements. */
    public DealToFoundation() {

        append(new DealStep(new ContainerTarget(SolitaireContainerTypes.Foundation)));

    }
}
