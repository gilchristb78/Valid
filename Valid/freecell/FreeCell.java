package org.combinators.solitaire.freecell;

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;
import java.awt.event.*;
import java.awt.Dimension;
import org.combinators.solitaire.freecell.controller.*;
import org.combinators.solitaire.freecell.model.*;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class FreeCell extends Solitaire implements SolvableSolitaire {

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
        // Single deck instantiated as is
        CardImages ci = getCardImages();
        // Single deck instantiated as is
        int cw = ci.getWidth();
        // Single deck instantiated as is
        int ch = ci.getHeight();
        // Basic start of pretty much any solitaire game that requires a deck.
        deck = new Deck("deck");
        int seed = getSeed();
        deck.create(seed);
        addModelElement(deck);
        for (int j = 0; j < 8; j++) {
            tableau[j] = new Column(tableauPrefix + (j + 1));
            addModelElement(tableau[j]);
            tableauView[j] = new ColumnView(tableau[j]);
        }
        for (int j = 0; j < 4; j++) {
            foundation[j] = new Pile(foundationPrefix + (j + 1));
            addModelElement(foundation[j]);
            foundationView[j] = new PileView(foundation[j]);
        }
        for (int j = 0; j < 4; j++) {
            reserve[j] = new FreeCellPile(reservePrefix + (j + 1));
            addModelElement(reserve[j]);
            reserveView[j] = new FreeCellPileView(reserve[j]);
        }
        tableauView[0].setBounds(10, 200, 73, 776);
        addViewWidget(tableauView[0]);
        tableauView[1].setBounds(98, 200, 73, 776);
        addViewWidget(tableauView[1]);
        tableauView[2].setBounds(186, 200, 73, 776);
        addViewWidget(tableauView[2]);
        tableauView[3].setBounds(274, 200, 73, 776);
        addViewWidget(tableauView[3]);
        tableauView[4].setBounds(362, 200, 73, 776);
        addViewWidget(tableauView[4]);
        tableauView[5].setBounds(450, 200, 73, 776);
        addViewWidget(tableauView[5]);
        tableauView[6].setBounds(538, 200, 73, 776);
        addViewWidget(tableauView[6]);
        tableauView[7].setBounds(626, 200, 73, 776);
        addViewWidget(tableauView[7]);
        foundationView[0].setBounds(400, 10, 73, 97);
        addViewWidget(foundationView[0]);
        foundationView[1].setBounds(488, 10, 73, 97);
        addViewWidget(foundationView[1]);
        foundationView[2].setBounds(576, 10, 73, 97);
        addViewWidget(foundationView[2]);
        foundationView[3].setBounds(664, 10, 73, 97);
        addViewWidget(foundationView[3]);
        reserveView[0].setBounds(10, 10, 73, 97);
        addViewWidget(reserveView[0]);
        reserveView[1].setBounds(98, 10, 73, 97);
        addViewWidget(reserveView[1]);
        reserveView[2].setBounds(186, 10, 73, 97);
        addViewWidget(reserveView[2]);
        reserveView[3].setBounds(274, 10, 73, 97);
        addViewWidget(reserveView[3]);
        for (int j = 0; j < 8; j++) {
            tableauView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            tableauView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            tableauView[j].setMouseAdapter(new ColumnController(this, tableauView[j]));
        }
        for (int j = 0; j < 4; j++) {
            foundationView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            foundationView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            foundationView[j].setMouseAdapter(new PileController(this, foundationView[j]));
        }
        for (int j = 0; j < 4; j++) {
            reserveView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            reserveView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            reserveView[j].setMouseAdapter(new FreeCellPileController(this, reserveView[j]));
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
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
        return "FreeCell";
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
        final GameWindow gw = Main.generateWindow(new FreeCell(), Deck.OrderBySuit);
        // properly exist program once selected.
        gw.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        gw.setVisible(true);
    }

    /**
     * A card is unneeded when no lower-rank cards of the opposite color remain
     * in the playing area.
     * <p>
     * Returns TRUE if cards of (rank-1) and opposite colored suit have both
     * already been played to the foundation.
     * <p>
     * Note that true is returned if an ACE is passed in.
     */
    protected boolean unneeded(int rank, int suit) {
        // error situation.
        if (rank == Card.ACE)
            return true;
        // see if cards of next lower rank and opposite color are both played
        // in the foundation.
        int countOppositeColorLowerRank = 0;
        for (int b = 0; b < foundation.length; b++) {
            if (foundation[b].empty())
                continue;
            Card bc = foundation[b].peek();
            if (bc.oppositeColor(suit) && bc.getRank() >= rank - 1) {
                countOppositeColorLowerRank++;
            }
        }
        // determine validity
        return (countOppositeColorLowerRank == 2);
    }

    // should be encapsulated out elsewhere since this is standard logic...
    public void tryAutoMoves() {
        Move m;
        do {
            m = autoMoveAvailable();
            if (m != null) {
                if (m.doMove(this)) {
                    pushMove(m);
                    refreshWidgets();
                } else {
                    // ERROR. Break now!
                    break;
                }
            }
        } while (m != null);
    }

    /**
     * For now, no automoves just yet...
     */
    public Move autoMoveAvailable() {
        // 1. First see if any columnBaseMove allowed.
        for (int c = 0; c < tableau.length; c++) {
            if (tableau[c].empty())
                continue;
            if (tableau[c].rank() == Card.ACE) {
                // find the empty destination pile
                Pile emptyDest = null;
                for (int i = 0; i < foundation.length; i++) {
                    if (foundation[i].empty()) {
                        emptyDest = foundation[i];
                    }
                }
                // SANITY CHECK.
                if (emptyDest == null) {
                    throw new IllegalStateException("ACE is available to play but no open destination piles.");
                }
                return new PotentialMoveCardFoundation(tableau[c], emptyDest);
            }
            Card cc = tableau[c].peek();
            // try to find a destination it goes to.
            Move theMove = null;
            boolean foundMove = false;
            for (int b = 0; b < foundation.length; b++) {
                theMove = new PotentialMoveCardFoundation(tableau[c], foundation[b]);
                if (theMove.valid(this)) {
                    foundMove = true;
                    break;
                }
            }
            // (note: for a valid move we know that the 2H has been played).
            if (foundMove) {
                if (unneeded(cc.getRank(), cc.getSuit())) {
                    int otherSuit = cc.getSuit();
                    if ((otherSuit == Card.CLUBS) || (otherSuit == Card.SPADES)) {
                        // arbitrary RED
                        otherSuit = Card.HEARTS;
                    } else {
                        // arbitrary BLACK
                        otherSuit = Card.CLUBS;
                    }
                    // now go down one more level
                    if (unneeded(cc.getRank() - 1, otherSuit)) {
                        return theMove;
                    }
                }
            }
        }
        // 2. Second see if any FreeCellBaseMove allowed.
        Move theMove = null;
        boolean foundMove = false;
        Card bc = null;
        for (int f = 0; f < reserve.length; f++) {
            if (reserve[f].empty())
                continue;
            // try to find a destination it goes to.
            for (int b = 0; b < foundation.length; b++) {
                theMove = new PotentialMoveCardFoundation(reserve[f], foundation[b]);
                if (theMove.valid(this)) {
                    bc = reserve[f].peek();
                    foundMove = true;
                    break;
                }
            }
            if (foundMove) {
                if (unneeded(bc.getRank(), bc.getSuit())) {
                    int otherSuit = bc.getSuit();
                    if ((otherSuit == Card.CLUBS) || (otherSuit == Card.SPADES)) {
                        // arbitrary RED
                        otherSuit = Card.HEARTS;
                    } else {
                        // arbitrary BLACK
                        otherSuit = Card.CLUBS;
                    }
                    // ACEs can be moved immediately...
                    if (bc.getRank() == Card.ACE) {
                        return theMove;
                    }
                    // now go down one more level
                    if (unneeded(bc.getRank() - 1, otherSuit)) {
                        return theMove;
                    }
                }
                // no move allowed.
                return null;
            }
        }
        // if nothing found, stop
        if (!foundMove) {
            theMove = null;
        }
        return theMove;
    }

    public boolean validColumn(Column column) {
        return column.alternatingColors() && column.descending();
    }

    public java.util.Enumeration<Move> availableMoves() {
        java.util.Vector<Move> v = new java.util.Vector<Move>();
        // try to build card to foundation
        for (Column c : tableau) {
            for (Pile p : foundation) {
                PotentialMoveCardFoundation pmcf = new PotentialMoveCardFoundation(c, p);
                if (pmcf.valid(this)) {
                    v.add(pmcf);
                }
            }
        }
        // try to move cards from free cell to foundation
        for (Pile s : reserve) {
            for (Pile d : foundation) {
                PotentialMoveCardFoundation pmcf = new PotentialMoveCardFoundation(s, d);
                if (pmcf.valid(this)) {
                    v.add(pmcf);
                }
            }
        }
        // than the destination.
        for (Column s : tableau) {
            for (Column d : tableau) {
                // don't waste time moving a single card from one reserve to another empty one
                if ((s != d) && !(s.count() == 1 && d.count() == 0)) {
                    PotentialMoveColumn pmc = new PotentialMoveColumn(s, d);
                    if (pmc.valid(this)) {
                        v.add(pmc);
                    }
                }
            }
        }
        // move smallest facing up column card to a free pile
        Column lowest = null;
        for (Column s : tableau) {
            if (s.count() > 0) {
                if (lowest == null) {
                    lowest = s;
                } else if (s.rank() < lowest.rank()) {
                    lowest = s;
                }
            }
        }
        if (lowest != null) {
            for (Pile p : reserve) {
                if (p.count() == 0) {
                    PotentialMoveColumn ppc = new PotentialMoveColumn(lowest, p);
                    v.add(ppc);
                    break;
                }
            }
        }
        return v.elements();
    }

    IntegerView scoreView;

    IntegerView numLeftView;

    public Deck deck;

    DeckView deckView;

    public Column[] tableau = new Column[8];

    public static final String tableauPrefix = "tableau";

    public ColumnView[] tableauView = new ColumnView[8];

    public Pile[] foundation = new Pile[4];

    public static final String foundationPrefix = "foundation";

    public PileView[] foundationView = new PileView[4];

    public FreeCellPile[] reserve = new FreeCellPile[4];

    public static final String reservePrefix = "reserve";

    public FreeCellPileView[] reserveView = new FreeCellPileView[4];

    @Override
    public Dimension getPreferredSize() {
        // default starting dimensions...
        return new Dimension(769, 976);
    }
}
