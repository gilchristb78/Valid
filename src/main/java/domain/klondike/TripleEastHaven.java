package domain.klondike;

import domain.Container;
import domain.Stock;

/**
 * Like EastHaven but with three decks.
 */
public class TripleEastHaven extends EastHaven {
    private Stock stock;

    /**
     * Require three decks.
     */
    @Override
    protected Container getStock() {
        if (stock == null) { stock = new Stock(3); }
        return stock;
    }

    public TripleEastHaven() {
        super("TripleEastHaven");
    }
}
