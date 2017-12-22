package domain;

public class Row extends Column {

    /** Row is horizontally oriented. */
    public Row() {
        setVerticalOrientation(false);
    }

    public boolean viewOneAtATime() { return false; }
}
