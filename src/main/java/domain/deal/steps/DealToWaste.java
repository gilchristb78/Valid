package domain.deal.steps;

import domain.SolitaireContainerTypes;
import domain.deal.ContainerTarget;
import domain.deal.Deal;
import domain.deal.DealStep;
import domain.deal.Payload;

public class DealToWaste extends Deal {

    /** Deal 1 face-up card to each of the Waste elements. */
    public DealToWaste() {
        this(1);
    }

    /** Deal n face-up cards to each of the Waste elements. */
    public DealToWaste(int n) {

        // Each one gets n faceup Cards
        Payload payload = new Payload(n, true);
        append(new DealStep(new ContainerTarget(SolitaireContainerTypes.Waste), payload));
    }
}
