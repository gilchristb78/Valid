package domain.constraints;

/**
 * Determines whether a stack of cards is empty.
 */
public class ElementEmpty extends UnaryConstraintExpr {

    public ElementEmpty (String element) {
        super(element);
    }
}
