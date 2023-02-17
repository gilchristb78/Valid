package org.combinators.solitaire.flake_two_decks;

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;
import java.awt.event.*;
import java.awt.Dimension;
import org.combinators.solitaire.flake_two_decks.controller.*;
import org.combinators.solitaire.flake_two_decks.model.*;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class Flake_two_decks extends Solitaire {

    /**
     * Enable refinements to determine whether game has been won.
     */
    public boolean hasWon() {
        boolean hasWon = true;
        {
            int _ct = 0;
            for (Stack st : waste) {
                _ct += st.count();
            }
            if (_ct != 104) {
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
        for (int j = 0; j < 6; j++) {
            tableau[j] = new Column(tableauPrefix + (j + 1));
            addModelElement(tableau[j]);
            tableauView[j] = new ColumnView(tableau[j]);
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
        tableauView[0].setBounds(120, 20, 73, 485);
        addViewWidget(tableauView[0]);
        tableauView[1].setBounds(208, 20, 73, 485);
        addViewWidget(tableauView[1]);
        tableauView[2].setBounds(296, 20, 73, 485);
        addViewWidget(tableauView[2]);
        tableauView[3].setBounds(384, 20, 73, 485);
        addViewWidget(tableauView[3]);
        tableauView[4].setBounds(472, 20, 73, 485);
        addViewWidget(tableauView[4]);
        tableauView[5].setBounds(560, 20, 73, 485);
        addViewWidget(tableauView[5]);
        wasteView[0].setBounds(15, 137, 73, 97);
        addViewWidget(wasteView[0]);
        for (int j = 0; j < 6; j++) {
            tableauView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            tableauView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            tableauView[j].setMouseAdapter(new ColumnController(this, tableauView[j]));
        }
        for (int j = 0; j < 1; j++) {
            wasteView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            wasteView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            wasteView[j].setMouseAdapter(new WastePileController(this, wasteView[j]));
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
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
        return "Flake_two_decks";
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
        final GameWindow gw = Main.generateWindow(new Flake_two_decks(), Deck.OrderBySuit);
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

    public Column[] tableau = new Column[6];

    public static final String tableauPrefix = "tableau";

    public ColumnView[] tableauView = new ColumnView[6];

    public Deck deck;

    DeckView deckView;

    public WastePile[] waste = new WastePile[1];

    public static final String wastePrefix = "waste";

    public WastePileView[] wasteView = new WastePileView[1];
}
