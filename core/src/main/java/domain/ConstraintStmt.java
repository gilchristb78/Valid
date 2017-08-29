package domain;

import java.util.*;

import domain.constraints.*;

/**
 * Records logic for allowed.
 *
 */
public abstract class ConstraintStmt { 

   final ConstraintExpr expr;

   public ConstraintStmt(ConstraintExpr e) { 
     this.expr = e;
   }

   public ConstraintExpr getExpr() {
        return expr ;
    }
}

