package domain.deal;

/**
 * What is dealt.
 */
public class Payload {

    public final boolean   faceUp;
    public final int       numCards;

    /** A single face up card. */
    public Payload() {
        numCards = 1;
        faceUp = true;
    }

    /** A number of cards dealt either faceup or facedown. */
    public Payload (int numCards, boolean faceUp) {
        this.numCards = numCards;
        this.faceUp = faceUp;
    }


}
