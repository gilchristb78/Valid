package domain.klondike;

import domain.Container;
import domain.SolitaireContainerTypes;
import domain.Stock;
import domain.deal.ContainerTarget;
import domain.deal.Deal;
import domain.deal.DealStep;
import domain.deal.Payload;

/**
 * Like EastHaven but with two decks.
 */
public class DoubleEastHaven extends EastHaven {
    private Stock stock;

    /**
     * Require two decks.
     */
    @Override
    protected Container getStock() {
        if (stock == null) { stock = new Stock(2); }
        return stock;
    }

    public DoubleEastHaven() {
        super("DoubleEastHaven");
    }
}
