package domain.klondike;

import domain.Constraint;
import domain.Solitaire;
import domain.SolitaireContainerTypes;
import domain.constraints.*;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.ui.CalculatedPlacement;
import domain.ui.HorizontalPlacement;
import domain.ui.Layout;
import domain.ui.VerticalPlacement;

import java.awt.*;

/**
 * rules are same. Only layout is different
 */
public class SmallHarp extends KlondikeDomain {

    private Layout layout;

    // Active SBT bug. Once you have an overridden method in a class, you can't remove it ... ever....
    // there should be way of clearing target/ files, but not going to try it...
    // https://github.com/sbt/zinc/issues/292

    /** Note these are redundant, but they need to be left in because of the above error. */
    @Override
    public Constraint buildOnEmptyTableau(MoveInformation bottom) {
        return super.buildOnEmptyTableau(bottom);
    }

    /** As long as SUIT is different (and lower rank) then ok. */
    public Constraint buildOnTableau(MoveInformation bottom) {
        return super.buildOnTableau(bottom);
    }

    @Override
    public Layout getLayout() {
        if (layout == null) {
            Point[] anchors = new Point[7];
            for (int i = 0; i < 7; i++) {
                anchors[i] = new Point(520 - 80*i, 40);
            }

            layout = new Layout()
                .add(SolitaireContainerTypes.Tableau,
                    new CalculatedPlacement(anchors, Solitaire.card_width, Solitaire.card_height+13*Solitaire.card_overlap))
                .add(SolitaireContainerTypes.Foundation,
                    new VerticalPlacement(new Point (600, 40), Solitaire.card_width, Solitaire.card_height, Solitaire.card_gap))
                .add(SolitaireContainerTypes.Stock, new HorizontalPlacement(new Point(580, 40 + 4*(Solitaire.card_height + Solitaire.card_gap)),
                    Solitaire.card_width,  Solitaire.card_height,  Solitaire.card_gap))
                .add(SolitaireContainerTypes.Waste, new HorizontalPlacement(new Point(500, 40 + 4*(Solitaire.card_height + Solitaire.card_gap)),
                    Solitaire.card_width,  Solitaire.card_height,  Solitaire.card_gap));
        }

        return layout;
    }

    public SmallHarp() {
        super("SmallHarp");
    }
}
