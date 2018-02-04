package domain.klondike;

import domain.Solitaire;
import domain.SolitaireContainerTypes;
import domain.ui.HorizontalPlacement;
import domain.ui.Layout;

import java.awt.*;

/**
 * Layout of Klondike assumes the existence of a number of container types.
 *
 *
 * TODO: This could be more compositional.
 */
public class KlondikeLayout extends Layout {


    public KlondikeLayout() {
        add(SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point(40, 200),
                Solitaire.card_width, 13*Solitaire.card_height, Solitaire.card_gap));

        add(SolitaireContainerTypes.Stock, new HorizontalPlacement(new Point(15, 20),
                Solitaire.card_width,  Solitaire.card_height,  Solitaire.card_gap));

        add(SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(293, 20),
                Solitaire.card_width,  Solitaire.card_height,  Solitaire.card_gap));

        add(SolitaireContainerTypes.Waste, new HorizontalPlacement(new Point(95, 20),
                Solitaire.card_width,  Solitaire.card_height,  Solitaire.card_gap));

    }
}
