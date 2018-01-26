package domain.deal;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Model the way that cards are initially dealt in the solitaire variation.
 *
 * Break the dealing process into a number of steps. Each step consists of
 * a target, a number of cards, and whether the cards are faceup/facedown.
 *
 * Each DealStep represents cards to be dealt. Note that it may be the case that
 * one wants to 'filter out' cards and deal them individually later, or even remove
 * from the game.
 *
 * 1. Beleaguered Castle deals out all cards BUT ACES and places them in foundation
 * 2. Archway deals special cards to reserves, aces to AcesUp, kinds to Kings Down,
 *    and remaining cards in the Tableau.
 *
 * Need to find way to identify cards to go to Foundations. Could be fixed at ACES. Could be
 * the first card, or first four cards, etc...
 *
 * One idea is to have a FILTER pass which takes a deck and returns a deck, placing the special cards
 * at the end of the deck, thus allowing you to deal LAST [alternatively, you could make it first]
 */
public class Deal implements Iterable<Step> {

    List<Step> steps = new ArrayList<>();

    public Deal() {

    }

    /** Add all deal steps after our own. */
    public Deal append(Deal other) {
        this.steps.addAll(other.steps);
        return this;
    }

    /**
     * Have this method return 'this' to make it possible to easily chain together programming tasks.
     *
     * @param s
     * @return self to be able to chain together programmatically for convenience
     */
    public Deal append(Step s) {
        this.steps.add(s);
        return this;
    }

    /** Helper used within subclass to append deal steps after our own. */
    protected void appendSteps(Deal other) {
        this.steps.addAll(other.steps);
    }

    public Iterator<Step> iterator() {
        return steps.iterator();
    }
}
