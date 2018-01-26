package domain.ui.layouts;

import domain.Solitaire;
import domain.SolitaireContainerTypes;
import domain.ui.HorizontalPlacement;
import domain.ui.Layout;

import java.awt.*;

public class StockTableauPilesLayout extends Layout {

    public StockTableauPilesLayout() {
        // Stock in upper left corner.
        add(SolitaireContainerTypes.Stock, new HorizontalPlacement(new Point(15, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap));

        // Tableau on right as piles.
        add(SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point (120, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap));
    }
}
