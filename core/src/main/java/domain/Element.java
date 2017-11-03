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

    public boolean getVerticalOrientation () {
        return this.verticalOrientation;
    }
}
