package domain.deal;

import domain.deal.map.MapCard;

/**
 * The next N cards (based on payload) can be dealt to a target container by
 * mapping a card to its location based on a map function.
 */
public class MapStep implements Step {
    public final MapCard map;
    public final ContainerTarget target;
    public final Payload payload;

    /** Will apply given constraint to a specific card. */
    public MapStep(ContainerTarget target, MapCard map, Payload payload) {
        this.target = target;
        this.map = map;
        this.payload = payload;
    }
}