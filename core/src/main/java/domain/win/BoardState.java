package domain.win;

import domain.ContainerType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Given a container, sum up the total number of cards and verify it meets total
 */
public class BoardState implements WinningLogic, Iterable<BoardStatePair> {

    public final List<BoardStatePair> elements = new ArrayList<>();

    public void add(ContainerType type, int total) {
       elements.add(new BoardStatePair (type, total));
    }

    public Iterator<BoardStatePair> iterator() {
        return elements.iterator();
    }
}
