package domain.constraints;

import java.util.*;
import domain.*;

/**
 *  Used to record AST directly. Stmt is either 'if-then-else' or 'return'
 */
public class ReturnConstraint extends ConstraintStmt {

    public ReturnConstraint (ConstraintExpr c) {
        super(c);
    }

}
