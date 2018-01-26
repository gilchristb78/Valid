package domain.ui;

import domain.Solitaire;
import domain.SolitaireContainerTypes;

import java.awt.*;

public class ReserveFoundationTableauLayout extends Layout {

    public ReserveFoundationTableauLayout() {
        add (SolitaireContainerTypes.Reserve, new HorizontalPlacement(new Point(15, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap));

        add (SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(390, 20),
                Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap));

        add (SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point (15, 137),
                Solitaire.card_width,13*Solitaire.card_height, Solitaire.card_gap));
    }
}
