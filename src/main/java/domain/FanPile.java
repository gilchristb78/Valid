package domain;

public class FanPile extends Column {

    public final int numToShow;

    /** FanPile is horizontally oriented. */
    public FanPile(int n) {
        setVerticalOrientation(false);
        this.numToShow = n;
    }

    public boolean viewOneAtATime() { return false; }
}