package domain.ui;

import domain.Container;
import domain.Solitaire;
import domain.SolitaireContainerTypes;

import java.awt.Point;

/**
 * Place reserve starting from left upper corner. Then leave gap for foundation.
 *
 * Then starting below both (flush left) do the tableau
 */
public class ReserveFoundationTableauLayout extends Layout {

    public ReserveFoundationTableauLayout(Container reserve, Container foundation, Container tableau) {
        add (SolitaireContainerTypes.Reserve, new HorizontalPlacement(new Point(15, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap));

        int total = reserve.size()*(Solitaire.card_width + Solitaire.card_gap);
        add (SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(15 + total, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap));

        add (SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point (15, Solitaire.card_height+40),
                Solitaire.card_width,13*Solitaire.card_height, Solitaire.card_gap));
    }
}
