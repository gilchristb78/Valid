package domain.deal.steps;

import domain.SolitaireContainerTypes;
import domain.deal.ContainerTarget;
import domain.deal.Deal;
import domain.deal.DealStep;
import domain.deal.Payload;

public class DealToTableau extends Deal {

    /** Deal 1 face-up card to each of the Tableau elements. */
    public DealToTableau() {
        this(1);
    }

    /** Deal n face-up cards to each of the Tableau elements. */
    public DealToTableau(int n) {

        // Each one gets n faceup Cards
        Payload payload = new Payload(n, true);
        append(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), payload));

    }
}
