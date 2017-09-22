package domain.constraints;

/**
 * Do two elements have the same suit.
 */
public class SameSuit extends BinaryConstraintExpr {

    public SameSuit (String e1, String e2) {
        super(e1, e2);
    }
}
