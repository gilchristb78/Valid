package domain.constraints;

/**
 * Constrain element e1 to be of higher rank than element e2.
 */
public class HigherRank extends BinaryConstraintExpr {

    public HigherRank (String e1, String e2) {
        super(e1, e2);
    }
}
