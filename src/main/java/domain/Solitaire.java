package domain;

import java.awt.*;
import java.util.*;
import java.util.List;

import domain.deal.Deal;
import domain.ui.Layout;
import domain.ui.NonexistentPlaceement;
import domain.ui.PlacementGenerator;
import domain.ui.View;
import domain.win.ScoreAchieved;
import domain.win.WinningLogic;

/**

 This class models the top-level domain of the solitaire application
 space.

 Here is where you would find accurate descriptions of each variation,
 and it might be instructive to grab a handbook of solitaire variations
 and then convert the description "word for word" into different
 instances of the domain model.

 To convert a domain Model into a specific target, there needs to be
 a separate entity that traverses the structure, and this is handled
 by the Scala combinators.

 The classes within the domain reflect a deep understanding of the
 domain. It may not be necessary to name the classes according to
 any existing name of classes.

 User Experience Model defines the interaction, explaining which
 mouse events (Press, Click, Release) are responsible for which
 moves.

 A variation may have automoves computed (that is, it defines
 a method tryAutoMoves() in the base class. Record this fact in
 the domain.

 A domain can be programmatically constructed from scratch, or
 could be derived from a Family. The KlondikeFamily is such an
 example.

 */

public abstract class Solitaire {
    /** Every Solitaire game has its own name. */
    public final String name;

    /**
     * Construct variation
     *
     * Also register all known special classes. Not sure this is in the right place...
     * @param name
     */
    public Solitaire (String name) {
        this.name = name;
        registerElements();
    }

    /** User-defined containers can be specified as needed in this map. */
    public final Map <ContainerType, Container> structure = new Hashtable<>();

    /** Layout keeps track of the placement of all containers. Handled by subclasses. */
    //protected Layout layout = null;
    //public void setLayout(Layout layout) { this.layout = layout; }
    public abstract Layout getLayout(); //  { return layout; }

    /** Get the name for a given container type. */
    public Container getByType (ContainerType typ) {
        for (ContainerType ct : structure.keySet()) {
            if (ct == typ) {
                return structure.get(ct);
            }
        }

        return null;   // replace with Option[]
    }

    /** Are automoves available. */
    protected boolean autoMovesAvailable = false;
    public boolean hasAutoMoves() { return autoMovesAvailable; }
    public void setAutoMoves(boolean b) { autoMovesAvailable = b; }

    /** Is this solvable. */
    protected boolean solvable = false;
    public boolean isSolvable() { return solvable; }
    public void setSolvable(boolean b) { solvable = b;}

    /** Deal information. Handled by subclasses. */
    //protected Deal deal = new Deal();
    public abstract Deal getDeal();  //  { return deal; }
    //public void setDeal (Deal d) { deal = d;}
    //protected void addDealStep(Step step) { deal.add(step); }

    /** Winning Logic. */
    protected WinningLogic logic = new ScoreAchieved(52);  // default seems reasonable
    public void setLogic(WinningLogic logic) { this.logic = logic; }
    public WinningLogic getLogic() { return logic; }

    /** Rules for the game. */
    protected Rules rules = new Rules();
    public Rules getRules() { return rules; }

    public static final int card_width = 73;
    public static final int card_height = 97;
    public static final int card_overlap = 22;    // visible distance in columns.

    /** Common separation between widgets in layout. */
    public static final int card_gap = 15;

    /** Simplify writing of domain rules. */
    protected void addDragMove(Move m) { rules.addDragMove(m); }
    protected void addPressMove(Move m) { rules.addPressMove(m); }
    protected void addClickMove(Move m) { rules.addClickMove(m); }

    /** Occasionally a variation needs its own model elements, which are registered here. */
    protected List<Element> specializedElements = new ArrayList<>();
    protected List<View> specializedViews = new ArrayList<>();
    protected void registerElementAndView(Element e, View v) {
        specializedElements.add(e);
        specializedViews.add(v);   // e.getClass().getSimpleName() + "View");
    }
    protected void registerElement(Element e) { specializedElements.add(e); }
    protected void registerView(View v) { specializedViews.add(v); }


    public Iterator<Element> domainElements() { return specializedElements.iterator(); }
    public Iterator<View> domainViews() { return specializedViews.iterator(); }

    /**
     * Compute minimum width and height required to realize this variation. Computes based on
     * the associated placement of layouts.
     *
     * Make sure to add 20 pixels on the right for a buffer.
     */
    public Dimension getMinimumSize() {
        Dimension min = new Dimension(0,0);
        for (ContainerType ct : structure.keySet()) {
            PlacementGenerator it = getLayout().get(ct).orElse(new NonexistentPlaceement());
            it.reset(structure.get(ct).size());
            while (it.hasNext()) {
                Widget w = it.next();
                if (w.y + w.height > min.height) {
                    min.height = w.y + w.height;
                }
                if (w.x + w.width + 20 > min.width) {
                    min.width = w.x + w.width + 20;
                }
            }
        }

        return min;
    }

    /**
     * Record the layout for the given container in this Solitaire variation and add container to domain.
     *
     * note we could have classes whose job is to call these low-level functions to properly
     * layout the containers based no stylized variations.
     */
    public void placeContainer(Container container) {
        if (getLayout() == null) { return; } // HACK. TODO: REMOVE
        getLayout().layoutContainer(container.type, container);
        structure.put(container.type, container);
    }
    public void placeContainer(Container container, PlacementGenerator plg) {
        placeContainer(container);
        //Only Here to allow to compile during transition
        //
    }
    /**
     * Some containers have no visible presence (as detected by no widgets in placements).
     *
     * TODO: Should this have ContainerType as argument
     */
    public boolean isVisible(Container c) {
        return getLayout().isVisible(c.type);
    }

    /**
     * Retrieve Iterator of Widgets reflecting the elements in the container.
     *
     * TODO: Should this have ContainerType as argument
     *
     * @return    Widget objects, each with their boundaries and index into the container.
     */
    public Iterator<Widget> placements(Container c) {
        return getLayout().placements(c.type);
    }

    /**
     * Return iterator of containers in Solitaire variation.
     *
     * @return
     */
    public Iterator<Container> containers() {
        return structure.values().iterator();
    }

    /**
     * Remove the move from the iterator which matches by name.
     *
     * Used by solitaire variations to eliminate an existing move from another variation.
     * @param moves
     * @param name
     * @return true if removed a move with given name; false otherwise.
     */
    protected boolean remove(Iterator<Move> moves, String name) {
        while (moves.hasNext()) {
            Move exist = moves.next();
            if (exist.getName().equals(name)) {
                moves.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Disable a move by turning its source and target constraint into Falsehood.
     *
     * @param moves
     * @param name
     * @return
     */
    protected boolean prevent(Iterator<Move> moves, String name) {
        while (moves.hasNext()) {
            Move exist = moves.next();
            if (exist.getName().equals(name)) {
                exist.prevent();
                return true;
            }
        }

        return false;
    }

    /**
     * Each solitaire game has the responsibility to register those elements (i.e., WastePile, FreePile) that
     * are used by the variation.
     *
     * By default, no elements are required.
     */
    public void registerElements() { }
}
