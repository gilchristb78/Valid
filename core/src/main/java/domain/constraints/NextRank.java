package domain.constraints;

/**
 * Is rank of element e1 equal to 1 + rank of element 2.
 *
 */
public class NextRank extends BinaryConstraintExpr {

    public NextRank (String e1, String e2) {
	    super(e1, e2);
    }
}
