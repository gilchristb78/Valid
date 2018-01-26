package domain.klondike;

import domain.SolitaireContainerTypes;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.DealToWaste;

public class KlondikeDeal extends Deal {

    /**
     * TODO: This could be more compositional
     */
    public KlondikeDeal() {

        // each of the BuildablePiles gets a number of facedown cards, 0 to first Pile, 1 to second pile, etc...
        // don't forget zero-based indexing.
        for (int pileNum = 1; pileNum < 7; pileNum++) {
            append(new DealStep(new ElementTarget(SolitaireContainerTypes.Tableau, pileNum), new Payload(pileNum, false)));
        }

        // finally each one gets a single faceup Card, and deal one to waste pile
        //add(new DealStep(new ContainerTarget(SolitaireContainerTypes.Tableau), new Payload()));
        append(new DealToTableau());
        append(new DealToWaste());

    }
}
