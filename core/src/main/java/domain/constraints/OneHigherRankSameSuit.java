package domain.constraints;


/**
 * A Constraint which is based on two elements having one higher rank, same suit.
 * Do not use, but instead build up using {@link AndConstraint}
 */
@Deprecated
public class OneHigherRankSameSuit extends BinaryConstraintExpr {

    public OneHigherRankSameSuit (String e1, String e2) {
        super(e1, e2);
    }
}
