package domain.constraints;

/**
 * Do two elements have the same rank.
 */
public class SameRank extends BinaryConstraintExpr {

    public SameRank (String e1, String e2) {
	    super(e1, e2);
    }
}
