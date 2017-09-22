package domain.constraints;

/**
 * Is element e2 the next rank, regardless of suit.
 */
public class NextRank extends BinaryConstraintExpr {

    public NextRank (String e1, String e2) {
	    super(e1, e2);
    }
}
