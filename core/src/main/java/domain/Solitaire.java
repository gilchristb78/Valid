package domain;

import java.awt.*;
import java.util.*;
import domain.ui.*;

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

public class Solitaire {

    /** User-defined containers can be specified as needed in this map. */
    public final Map <ContainerType, Container> containers = new Hashtable<ContainerType, Container>();

    /** Only solitaire domain needs to know the 'name' of each type. */
    public final Map <ContainerType, String> names = new Hashtable<>();

    /** Give the designated container type a name */
    public void name (ContainerType ct, String name) {
        names.put(ct, name);
    }

    /** Get the name for a given container type. */
    public String getName (ContainerType ct) {
        return names.get(ct);
    }

    boolean autoMovesAvailable = false;
    public boolean hasAutoMoves() { return autoMovesAvailable; }
    public void setAutoMoves(boolean b) { autoMovesAvailable = b; }

    Rules         rules;
    public Rules  getRules() { return rules; }
    public void   setRules(Rules r) { rules = r; }

    public static final int card_width = 73;
    public static final int card_height = 97;

    /** Common separation between widgets in layout. */
    public static final int card_gap = 15;
}
