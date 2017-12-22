package domain;

public abstract class Element {

    /** Orientation. By default, vertical downwards. */
    boolean verticalOrientation = true;

    /**
     * Set orientation of cards within multi-card shapes.
     *
     * @param b
     */
    public void setVerticalOrientation (boolean b) {
        this.verticalOrientation = b;
    }

    /** Retrieve orientation of card (true=vertical like a column, false=horizontal like a row). */
    public boolean getVerticalOrientation () {
        return this.verticalOrientation;
    }

    /** Determines whether view is one card at a time (like Pile) or multiple (like Column, Row). */
    public abstract boolean viewOneAtATime();

}
