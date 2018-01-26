package domain.deal.steps;

import domain.constraints.IsAce;
import domain.deal.*;

public class FilterAces extends Deal {

    /** Extract the four aces and place them first. */
    public FilterAces() {
        this(4);
    }

    /** Extract first n aces and make them first. */
    public FilterAces(int n) {

        // Filter all aces out and add back on top
        append(new FilterStep(new IsAce(DealComponents.Card), 4));

    }
}
