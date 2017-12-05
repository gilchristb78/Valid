package domain.win;

/**
 * Win a game once a certain score is achieved.
 */
public class ScoreAchieved implements WinningLogic {

    public final int score;

    public ScoreAchieved (int score) {
        this.score = score;
    }
}
