@(rootPackage:Name,
    nameParameter:SimpleName,
    winParameter:Seq[Statement],
    initializeSteps:Seq[Statement])

package @Java(rootPackage);

// these are still too many to include all at once.

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class @Java(nameParameter) extends Solitaire {

    /** Enable refinements to determine whether game has been won. */
    public boolean hasWon() {
        @Java(winParameter)
        return false;
    }

    /**
     * Refinement determines initializations.
     */
    public void initialize() {
        @Java(initializeSteps)

        // Cover the Container for any events not handled by a widget:
        getContainer().setMouseMotionAdapter(new ks.common.controller.SolitaireMouseMotionAdapter(this));
        getContainer().setMouseAdapter(new ks.common.controller.SolitaireReleasedAdapter(this));
        getContainer().setUndoAdapter(new SolitaireUndoAdapter(this));
    }

    /**
     * Refinement determines name.
     */
    public String getName() {
        return "@nameParameter";   // special case to be handled in parser specially. Parser quotes.
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

class Entry {
    final Solitaire solitaire;

    Entry (Solitaire s) {
        solitaire = s;
    }

    public String toString() { return solitaire.getName(); }
}

    static DefaultListModel<Entry> register(Class<?> variation) {
        DefaultListModel<Entry> model = new DefaultListModel<>();
        java.io.File ksDir = new java.io.File(System.getProperty("user.home"), ".ks");
        java.io.File file = new java.io.File(ksDir, variation.getCanonicalName());

        if (!ksDir.exists()) {
            if (ksDir.mkdir()) {
                try { file.createNewFile(); } catch (java.io.IOException e) {}
            }
            @Java(nameParameter) myEntry = new @Java(nameParameter) ();
            model.addElement(myEntry.new Entry(myEntry));
            return model;
        } else {
            // try to load up all solitaire known variations
            java.io.File[] entries = ksDir.listFiles();
            if (entries == null) { entries = new java.io.File[0]; }  // what if there as file not dir?
            final @Java(nameParameter) myEntry = new @Java(nameParameter) ();
            for (java.io.File f : entries) {
                String name = f.getName();
                try {
                    Class clazz = Class.forName(name);
                    Solitaire sol = (Solitaire) clazz.newInstance();
                    model.addElement(myEntry.new Entry(sol));
                } catch (Exception e) {
                }
            }

            // see if we need to register our variation
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (java.io.IOException e) {
                }
                model.addElement(myEntry.new Entry(myEntry));
            }
        }

        return model;
    }

    // force to be able to launch directly.
    public static void main(String[] args) {
        DefaultListModel<Entry> model = register(@Java(nameParameter) .class);
        final JFrame jf = new JFrame();
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
        JButton jb = new JButton("Start");
        JList<Entry> jl = new JList<Entry>(model);
        jl.setSelectedIndex(0);
        jp.add(jb);
        jp.add(new JLabel("Available Variations"));
        jp.add(Box.createRigidArea(new Dimension(0, 5)));
        jp.add(jl);
        jp.add(Box.createRigidArea(new Dimension(0, 5)));
        jf.add(jp);
        jf.setSize(200, 200);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                // use temporary window as peer for retrieving deck images.
                CardImagesLoader.getDeck(jf, "oxymoron");

                jf.setVisible(false);
                final GameWindow gw = Main.generateWindow(((Entry)jl.getSelectedValue()).solitaire, Deck.OrderBySuit);
                // properly exist program once selected.
                gw.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        System.exit(0);
                    }
                });
                gw.setVisible(true);
            }
        });
    }
}
