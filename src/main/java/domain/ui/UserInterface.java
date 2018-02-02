package domain.ui;

import java.util.*;
import domain.*;

/**
 * Only invoke once Solitaire structure is in place with all rules.
 *
 * The rules determine what is possible. The different types of the
 * widgets determines the controllers.
 *
 *     Target    (press on Src, Release on Target)
 * Src x  x  x
 *
 *     Click
 * Src x
 *
 *     Dbl-click
 * Src x
 *
 *
 * Default as ignore.... once a Container is added to a UserInterface, 
 * then ignore registrations are inserted.
 */
public class UserInterface {

    public final Solitaire solitaire;
    public final static int    PRESS      = 1;
    public final static int    CLICK      = 2;
    public final static int    DBL_CLICK  = 3;   // possible
    public final static int    RELEASE    = 4;

    public UserInterface (Solitaire s) {
        this.solitaire = s;

//     process();
    }

    /**
     * Resports the names of Elements for which controllers need to
     * be constructed. Only Visible elements need to be constructed.
     *
     */
    public Iterator<String> controllers() {
        System.out.println (">> compute controllers");
        ArrayList<String> elements = new ArrayList<>();

        /** Get each of the containers registered for the solitaire game. */
        for (Container c : solitaire.structure.values()) {
            if (!solitaire.isVisible(c)) { continue; }

            Iterator<String> type_it = c.types();
            while (type_it.hasNext()) {
                String typ = type_it.next();
                if (!elements.contains(typ)) {
                    elements.add(typ);
                }
            }
        }

        return elements.iterator();
    }


}
