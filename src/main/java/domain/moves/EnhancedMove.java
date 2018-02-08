package domain.moves;

import domain.*;

import java.util.Optional;

/**
 * When multiple actions need to be taken, on doMove and undoMove, then use this to wrap
 * a single ActualMove; I think this will work recursively.
 *
 * For example, Score(SingleCardMove) reflects that score would be adjusted
 *
 * Stalactites might have Orientation(Score(SingleCardMove)) to reflect that the score is adjusted
 * and the orientation for the game is fixed.
 *
 * Note that the synthesized solution domain knows how to add the necessary statements for
 * doMove or undoMove.
 *
 * TODO: Not yet fully integrated. This is only subclass that directly extends Move; others
 * TODO: extend ActualMove
 */
public class EnhancedMove implements Move {

    /** The actual move being enhanced. */
    public final Move base;

    /**
     * Determine conditions for moving column of cards from src to target.
     */
    public EnhancedMove(Move base) {
        this.base = base;
    }

    /** By definition will allow multiple cards to be moved. */
    public boolean isSingleCardMove() { return base.isSingleCardMove(); }

    /** By definition, will only be moved to a specific destination. */
    public boolean isSingleDestination() { return base.isSingleDestination(); }

    /** Get element being moved. Hack to make work for FreeCell. */
    public Element getMovableElement() { return base.getMovableElement(); }

    /** Every move has a name. */
    public String getName() { return base.getName(); }

    /** Every move has constraints associated with it. */
    public Constraint constraints() { return base.constraints(); }

    public Element getSource() { return base.getSource(); }
    public Element getTarget() { return base.getTarget(); }

    public Container getSourceContainer() { return base.getSourceContainer(); }
    public Optional<Container> getTargetContainer() { return base.getTargetContainer(); }

    public Constraint getSourceConstraint() { return base.getSourceConstraint(); }
    public Constraint getTargetConstraint() { return base.getTargetConstraint(); }

    public void prevent() { base.prevent(); }
}
