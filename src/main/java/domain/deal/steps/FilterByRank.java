package domain.deal.steps;

import domain.Card.Ranks;
import domain.constraints.IsRank;
import domain.deal.Deal;
import domain.deal.DealComponents;
import domain.deal.FilterStep;

public class FilterByRank extends Deal {

    /** Extract the four cards of given rank and place them first. */
    public FilterByRank(Ranks rank) {
        this(rank, 4);
    }

    /** Extract first n cards of given rank and make them first. */
    public FilterByRank(Ranks rank, int n) {

        // Filter all aces out and add back on top
        append(new FilterStep(new IsRank(DealComponents.Card, rank), 4));

    }
}
