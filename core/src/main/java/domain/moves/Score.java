package domain.moves;

import domain.Move;

/**
 * When a move affects the score.
 *
 * on execution: score increments by number of cards moved
 * on undo: score decrements by number of cards moved
 */
public class Score extends EnhancedMove {

    public Score(Move base) {
        super(base);
    }
}
