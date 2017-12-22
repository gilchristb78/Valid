package domain.ui;

import domain.Solitaire;

import java.awt.*;

public class ReserveFoundationTableauLayout {

    /**
     * Reserve in upper left corner.
     */
    public PlacementGenerator reserve() {
        return new HorizontalPlacement(new Point(15, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap);
    }

    /**
     * Foundation in upper right corner.
     */
    public PlacementGenerator foundation() {
        return new HorizontalPlacement(new Point(390, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap);
    }

    /**
     * Tableau in lower half.
     */
    public PlacementGenerator tableau() {
        return new HorizontalPlacement(new Point (15, 137),
                Solitaire.card_width,13*Solitaire.card_height, Solitaire.card_gap);
    }

}
