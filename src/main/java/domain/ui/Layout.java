package domain.ui;

import domain.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Map container types to PlacementGenerator objects.
 *
 */
public class Layout {

    /** Map the layout by container type. */
    Map<ContainerType, PlacementGenerator> places = new HashMap<>();

    /** Number of elements within each container. Unfortunately need this when setting max within the reset method for PlacementGenerator. */
    Map<ContainerType, Integer> counts = new HashMap<>();

    /**
     * Record the layout for the given container in this Solitaire variation.
     *
     * note we could have classes whose job is to call these low-level functions to properly
     * layout the containers based no stylized variations.
     */
    public void layoutContainer(ContainerType ct, Container container) {
        counts.put(ct, container.size());
    }

    /** Some containers have no visible presence (as detected by no widgets in placements). */
    public boolean isVisible(ContainerType ct) {
        PlacementGenerator p = places.get(ct);
        if (p == null) { return false; } // not sure what to do, really

        // Each Placement generator can be reset, which allows them to be reused. To start
        // we need to set to the counts
        p.reset(counts.get(ct));
        return p.hasNext();
    }

    /**
     * Retrieve Iterator of Widgets reflecting the elements in the container.
     *
     * @return    Widget objects, each with their boundaries and index into the container.
     */
    public Iterator<Widget> placements(ContainerType ct) {
        PlacementGenerator p = places.get(ct);
        if (p == null) { return new NonexistentPlaceement(); } // not sure what to do, really

        p.reset(counts.get(ct));
        return p;
    }

    /**
     * Map the desired type to a PlacementGenerator, if it exists.
     *
     * @param type
     * @return
     */
    public Optional<PlacementGenerator> get(ContainerType type) {
        if (places.containsKey(type)) {
            return Optional.of(places.get(type));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Add new placement Generator for given type.
     *
     * @param type
     * @param place
     * @return  this, to allow for chaining
     */
    public Layout add(ContainerType type, PlacementGenerator place) {
        places.put(type, place);
        return this;
    }

    /**
     * A variation may choose to remove a given ContainerType from layout.
     *
     * @param type
     * @return   this, to allow chaining together of commands.
     */
    public Layout remove(ContainerType type) {
        if (places.containsKey(type)) {
            places.remove(type);
            counts.remove(type);
        }
        return this;
    }

}
