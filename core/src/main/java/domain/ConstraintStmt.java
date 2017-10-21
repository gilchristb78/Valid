package domain;

/**
 * Records logic for allowed.
 *
 */
@Deprecated
public abstract class ConstraintStmt { 

   final Constraint expr;

   public ConstraintStmt(Constraint e) {
     this.expr = e;
   }

   public Constraint getExpr() {
        return expr ;
    }
}

