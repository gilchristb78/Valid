package domain.deal;

public class DealStep implements Step {
    public final Target target;
    public final Payload payload;

    /** Deal a number of cards (either faceup or facedown) to all elements in target. */
    public DealStep (Target target, Payload payload) {
        this.target = target;
        this.payload = payload;
    }

    /**
     * By default, the payload is a faceup card.
     * @param target
     */
    public DealStep (Target target) {
        this (target, new Payload());
    }
}