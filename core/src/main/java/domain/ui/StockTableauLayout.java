package domain.ui;

import domain.Solitaire;

import java.awt.*;

public class StockTableauLayout {

    /**
     * Stock in upper left corner.
     */
    public PlacementGenerator stock() {
        return new HorizontalPlacement(new Point(15, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap);
    }


    /**
     * Tableau on right as full columns
     */
    public PlacementGenerator tableau() {
        return new HorizontalPlacement(new Point (120, 20),
                Solitaire.card_width, 13*Solitaire.card_height, Solitaire.card_gap);
    }


    /**
     * Tableau on right as just piles
     */
    public PlacementGenerator tableauAsPile() {
        return new HorizontalPlacement(new Point (120, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap);
    }
}
