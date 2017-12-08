package domain;

import java.awt.*;
import java.util.*;
import java.util.List;

import domain.deal.Deal;
import domain.deal.DealStep;
import domain.deal.Step;
import domain.ui.NonexistentPlaceement;
import domain.ui.PlacementGenerator;
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
 a separate entity that traverses the structure, perhaps as a Visitor,
 but perhaps not, and returns the appropriate entity.

 If a visitor, then each and every sub-element in the domain must either
 implement an interface or extend a class. Annoying but at least offers
 a standardized means of processing the domain model.

 Perhaps use standard Eclipse EMF notation? don't invent the wheel!

 The classes within the domain reflect a deep understanding of the
 domain. It may not be necessary to name the classes according to
 any existing name of classes.

 User Experience Model defines the interaction, explaining which
 mouse events (Press, Click, Release) are responsible for which
 moves.

 A variation may have automoves computed (that is, it defines
 a method tryAutoMoves() in the base class. Record this fact in
 the domain.

 */

public abstract class Solitaire {
    /** Every Solitaire game has its own name. */
    public final String name;

    public Solitaire (String name) {
        this.name = name;
    }

    /** User-defined containers can be specified as needed in this map. */
    public final Map <ContainerType, Container> containers = new Hashtable<>();

    /** Containers must be told where they are placed. */
    public final Map <Container, PlacementGenerator> places = new Hashtable<>();

    /** Get the name for a given container type. */
    public Container getByName (String name) {
        for (ContainerType ct : containers.keySet()) {
            if (ct.getName().equals(name)) {
                return containers.get(name);
            }
        }

        return null;   // replace with Option[]
    }

    /** Are automoves available. */
    boolean autoMovesAvailable = false;
    public boolean hasAutoMoves() { return autoMovesAvailable; }
    public void setAutoMoves(boolean b) { autoMovesAvailable = b; }

    /** Deal information. */
    Deal deal = new Deal();
    public Deal   getDeal() { return deal; }
    public void   setDeal (Deal d) { deal = d;}
    protected void addDealStep(Step step) { deal.add(step); }

    /** Winning Logic. */
    WinningLogic logic = new ScoreAchieved(52);  // default seems reasonable
    public void setLogic(WinningLogic logic) { this.logic = logic; }
    public WinningLogic getLogic() { return logic; }

    /** Rules for the game. */
    Rules rules = new Rules();
    public Rules  getRules() { return rules; }

    public static final int card_width = 73;
    public static final int card_height = 97;

    /** Common separation between widgets in layout. */
    public static final int card_gap = 15;

    /** Simplify writing of domain rules. */
    protected void addDragMove(Move m) { rules.addDragMove(m); }
    protected void addPressMove(Move m) { rules.addPressMove(m); }
    protected void addClickMove(Move m) { rules.addClickMove(m); }

    /** Occasionally a variation needs its own model elements, which are registered here. */
    List<Element> specializedElements = new ArrayList<>();
    protected void registerElement(Element e) { specializedElements.add(e); }
    public Iterator<Element> domainElements() { return specializedElements.iterator(); }

    /**
     * Compute minimum width and height required to realize this variation. Computes based on
     * the associated placement of layouts
     */
    public Dimension getMinimumSize() {
        Dimension min = new Dimension(0,0);
        for (Container c : containers.values()) {
            Iterator<Widget> it = placements(c);     // c.placements();
            while (it.hasNext()) {
                Widget w = it.next();
                if (w.y + w.height > min.height) {
                    min.height = w.y + w.height;
                }
                if (w.x + w.width > min.width) {
                    min.width = w.x + w.width;
                }
            }
        }

        return min;
    }

    /**
     * Record the layout for the given container in this Solitaire variation.
     *
     * note we could have classes whose job is to call these low-level functions to properly
     * layout the containers based no stylized variations.
     */
    public void placeContainer(Container c, PlacementGenerator generator) {
        places.put(c, generator);
    }

    /** Some containers have no visible presence (as detected by no widgets in placements). */
    public boolean isVisible(Container c) {
        PlacementGenerator p = places.get(c);
        if (p == null) { return false; } // not sure what to do, really

        p.reset(c.size());
        return p.hasNext();
    }

    /**
     * Retrieve Iterator of Widgets reflecting the elements in the container.
     *
     * @return    Widget objects, each with their boundaries and index into the container.
     */
    public Iterator<Widget> placements(Container c) {
        PlacementGenerator p = places.get(c);
        if (p == null) { return new NonexistentPlaceement(); } // not sure what to do, really

        p.reset(c.size());
        return p;
    }
}
