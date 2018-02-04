@(rootPackage:Name, nameParameter:SimpleName, extraFieldsOrMethods:Seq[BodyDeclaration[_]])

package @{Java(rootPackage)}.model;

import ks.common.games.Solitaire;
import ks.common.model.*;
import java.util.function.BooleanSupplier;

public class ConstraintHelper {

    /** Helper method for processing constraints. Uses BooleanSupplier
     * to avoid evaluating all constraints which would lead to exceptions.
     * These are now lazily evaluated. */
    public static boolean ifCompute(boolean guard, BooleanSupplier truth, BooleanSupplier falsehood) {
        if (guard) {
            return truth.getAsBoolean();
        } else {
            return falsehood.getAsBoolean();
        }
    }

    /** Extra solitaire-manipulating methods are inserted here. */
    @Java(extraFieldsOrMethods)

    /** Helper to be able to retrieve variation specific solitaire without external cast. */
    public static @Java(rootPackage).@Java(nameParameter) getVariation(Solitaire game) {
        return (@Java(rootPackage).@Java(nameParameter)) game;
    }
}
