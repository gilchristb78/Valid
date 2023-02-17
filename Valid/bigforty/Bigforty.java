package org.combinators.solitaire.bigforty;

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;
import java.awt.event.*;
import java.awt.Dimension;
import org.combinators.solitaire.bigforty.controller.*;
import org.combinators.solitaire.bigforty.model.*;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class Bigforty extends Solitaire {

    /**
     * Enable refinements to determine whether game has been won.
     */
    public boolean hasWon() {
        boolean hasWon = true;
        {
            int _ct = 0;
            for (Stack st : foundation) {
                _ct += st.count();
            }
            if (_ct != 52) {
                return false;
            }
        }
        if (hasWon) {
            return true;
        }
        return false;
    }

    /**
     * Refinement determines initializations.
     */
    public void initialize() {
        // Fields
        CardImages ci = getCardImages();
        int cw = ci.getWidth();
        int ch = ci.getHeight();
        for (int j = 0; j < 10; j++) {
            tableau[j] = new Column(tableauPrefix + (j + 1));
            addModelElement(tableau[j]);
            tableauView[j] = new ColumnView(tableau[j]);
        }
        for (int j = 0; j < 4; j++) {
            foundation[j] = new Pile(foundationPrefix + (j + 1));
            addModelElement(foundation[j]);
            foundationView[j] = new PileView(foundation[j]);
        }
        for (int j = 0; j < 1; j++) {
            waste[j] = new WastePile(wastePrefix + (j + 1));
            addModelElement(waste[j]);
            wasteView[j] = new WastePileView(waste[j]);
        }
        // Single deck instantiated as is
        // Basic start of pretty much any solitaire game that requires a deck.
        deck = new Deck("deck");
        int seed = getSeed();
        deck.create(seed);
        addModelElement(deck);
        tableauView[0].setBounds(15, 200, 73, 1261);
        addViewWidget(tableauView[0]);
        tableauView[1].setBounds(103, 200, 73, 1261);
        addViewWidget(tableauView[1]);
        tableauView[2].setBounds(191, 200, 73, 1261);
        addViewWidget(tableauView[2]);
        tableauView[3].setBounds(279, 200, 73, 1261);
        addViewWidget(tableauView[3]);
        tableauView[4].setBounds(367, 200, 73, 1261);
        addViewWidget(tableauView[4]);
        tableauView[5].setBounds(455, 200, 73, 1261);
        addViewWidget(tableauView[5]);
        tableauView[6].setBounds(543, 200, 73, 1261);
        addViewWidget(tableauView[6]);
        tableauView[7].setBounds(631, 200, 73, 1261);
        addViewWidget(tableauView[7]);
        tableauView[8].setBounds(719, 200, 73, 1261);
        addViewWidget(tableauView[8]);
        tableauView[9].setBounds(807, 200, 73, 1261);
        addViewWidget(tableauView[9]);
        foundationView[0].setBounds(293, 20, 73, 97);
        addViewWidget(foundationView[0]);
        foundationView[1].setBounds(381, 20, 73, 97);
        addViewWidget(foundationView[1]);
        foundationView[2].setBounds(469, 20, 73, 97);
        addViewWidget(foundationView[2]);
        foundationView[3].setBounds(557, 20, 73, 97);
        addViewWidget(foundationView[3]);
        wasteView[0].setBounds(95, 20, 73, 97);
        addViewWidget(wasteView[0]);
        deckView = new DeckView(deck);
        deckView.setBounds(15, 20, 73, 97);
        addViewWidget(deckView);
        for (int j = 0; j < 10; j++) {
            tableauView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            tableauView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            tableauView[j].setMouseAdapter(new ColumnController(this, tableauView[j]));
        }
        for (int j = 0; j < 4; j++) {
            foundationView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            foundationView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            foundationView[j].setMouseAdapter(new PileController(this, foundationView[j]));
        }
        for (int j = 0; j < 1; j++) {
            wasteView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            wasteView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            wasteView[j].setMouseAdapter(new WastePileController(this, wasteView[j]));
        }
        deckView.setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
        deckView.setUndoAdapter(new SolitaireUndoAdapter(this));
        deckView.setMouseAdapter(new DeckController(this, deckView));
        for (int i = 0; i < 4; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.waste(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        // Cover the Container for any events not handled by a widget:
        getContainer().setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
        getContainer().setMouseAdapter(new SolitaireReleasedAdapter(this));
        getContainer().setUndoAdapter(new SolitaireUndoAdapter(this));
    }

    /**
     * Refinement determines name.
     */
    public String getName() {
        // special case to be handled in parser specially. Parser quotes.
        return "Bigforty";
    }

    /**
     * Helper routine for setting default widgets. This is defined so that any future layer
     * can use this method to define a reasonable default set of controllers for the widget.
     */
    protected void setDefaultControllers(Widget w) {
        w.setMouseMotionAdapter(new ks.common.controller.SolitaireMouseMotionAdapter(this));
        w.setMouseAdapter(new ks.common.controller.SolitaireReleasedAdapter(this));
        w.setUndoAdapter(new SolitaireUndoAdapter(this));
    }

    // force to be able to launch directly.
    public static void main(String[] args) {
        final GameWindow gw = Main.generateWindow(new Bigforty(), Deck.OrderBySuit);
        // properly exist program once selected.
        gw.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        gw.setVisible(true);
    }

    public boolean validColumn(Column column) {
        return column.descending();
    }

    IntegerView scoreView;

    IntegerView numLeftView;

    public Column[] tableau = new Column[10];

    public static final String tableauPrefix = "tableau";

    public ColumnView[] tableauView = new ColumnView[10];

    public Pile[] foundation = new Pile[4];

    public static final String foundationPrefix = "foundation";

    public PileView[] foundationView = new PileView[4];

    public WastePile[] waste = new WastePile[1];

    public static final String wastePrefix = "waste";

    public WastePileView[] wasteView = new WastePileView[1];

    public Deck deck;

    DeckView deckView;

    @Override
    public Dimension getPreferredSize() {
        // default starting dimensions...
        return new Dimension(880, 1461);
    }
}
