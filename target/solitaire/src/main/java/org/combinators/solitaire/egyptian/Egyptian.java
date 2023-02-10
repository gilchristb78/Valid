package org.combinators.solitaire.egyptian;

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;
import java.awt.event.*;
import java.awt.Dimension;
import org.combinators.solitaire.egyptian.controller.*;
import org.combinators.solitaire.egyptian.model.*;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class Egyptian extends Solitaire {

    /**
     * Enable refinements to determine whether game has been won.
     */
    public boolean hasWon() {
        boolean hasWon = true;
        {
            int _ct = 0;
            for (Stack st : tableau) {
                _ct += st.count();
            }
            if (_ct != 0) {
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
        for (int j = 0; j < 8; j++) {
            foundation[j] = new Pile(foundationPrefix + (j + 1));
            addModelElement(foundation[j]);
            foundationView[j] = new PileView(foundation[j]);
        }
        // Multi-decks are constructed from stock size.
        // Basic start of pretty much any solitaire game that requires a deck.
        deck = new MultiDeck("deck", 2);
        int seed = getSeed();
        deck.create(seed);
        addModelElement(deck);
        for (int j = 0; j < 1; j++) {
            waste[j] = new WastePile(wastePrefix + (j + 1));
            addModelElement(waste[j]);
            wasteView[j] = new WastePileView(waste[j]);
        }
        tableauView[0].setBounds(15, 150, 73, 1261);
        addViewWidget(tableauView[0]);
        tableauView[1].setBounds(103, 150, 73, 1261);
        addViewWidget(tableauView[1]);
        tableauView[2].setBounds(191, 150, 73, 1261);
        addViewWidget(tableauView[2]);
        tableauView[3].setBounds(279, 150, 73, 1261);
        addViewWidget(tableauView[3]);
        tableauView[4].setBounds(367, 150, 73, 1261);
        addViewWidget(tableauView[4]);
        tableauView[5].setBounds(455, 150, 73, 1261);
        addViewWidget(tableauView[5]);
        tableauView[6].setBounds(543, 150, 73, 1261);
        addViewWidget(tableauView[6]);
        tableauView[7].setBounds(631, 150, 73, 1261);
        addViewWidget(tableauView[7]);
        tableauView[8].setBounds(719, 150, 73, 1261);
        addViewWidget(tableauView[8]);
        tableauView[9].setBounds(807, 150, 73, 1261);
        addViewWidget(tableauView[9]);
        foundationView[0].setBounds(210, 20, 73, 97);
        addViewWidget(foundationView[0]);
        foundationView[1].setBounds(298, 20, 73, 97);
        addViewWidget(foundationView[1]);
        foundationView[2].setBounds(386, 20, 73, 97);
        addViewWidget(foundationView[2]);
        foundationView[3].setBounds(474, 20, 73, 97);
        addViewWidget(foundationView[3]);
        foundationView[4].setBounds(562, 20, 73, 97);
        addViewWidget(foundationView[4]);
        foundationView[5].setBounds(650, 20, 73, 97);
        addViewWidget(foundationView[5]);
        foundationView[6].setBounds(738, 20, 73, 97);
        addViewWidget(foundationView[6]);
        foundationView[7].setBounds(826, 20, 73, 97);
        addViewWidget(foundationView[7]);
        deckView = new DeckView(deck);
        deckView.setBounds(15, 20, 73, 97);
        addViewWidget(deckView);
        wasteView[0].setBounds(93, 20, 73, 97);
        addViewWidget(wasteView[0]);
        for (int j = 0; j < 10; j++) {
            tableauView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            tableauView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            tableauView[j].setMouseAdapter(new ColumnController(this, tableauView[j]));
        }
        for (int j = 0; j < 8; j++) {
            foundationView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            foundationView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            foundationView[j].setMouseAdapter(new PileController(this, foundationView[j]));
        }
        deckView.setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
        deckView.setUndoAdapter(new SolitaireUndoAdapter(this));
        deckView.setMouseAdapter(new DeckController(this, deckView));
        for (int j = 0; j < 1; j++) {
            wasteView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            wasteView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            wasteView[j].setMouseAdapter(new WastePileController(this, wasteView[j]));
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 3; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 5; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 7; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 9; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 10; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 8; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[8].add(c);
        }
        for (int i = 0; i < 2; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[9].add(c);
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
        return "Egyptian";
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
        final GameWindow gw = Main.generateWindow(new Egyptian(), Deck.OrderBySuit);
        // properly exist program once selected.
        gw.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        gw.setVisible(true);
    }

    IntegerView scoreView;

    IntegerView numLeftView;

    public Column[] tableau = new Column[10];

    public static final String tableauPrefix = "tableau";

    public ColumnView[] tableauView = new ColumnView[10];

    public Pile[] foundation = new Pile[8];

    public static final String foundationPrefix = "foundation";

    public PileView[] foundationView = new PileView[8];

    public Deck deck;

    DeckView deckView;

    public WastePile[] waste = new WastePile[1];

    public static final String wastePrefix = "waste";

    public WastePileView[] wasteView = new WastePileView[1];

    @Override
    public Dimension getPreferredSize() {
        // default starting dimensions...
        return new Dimension(899, 1411);
    }
}
