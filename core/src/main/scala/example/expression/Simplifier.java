package example.expression;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.CloneVisitor;

/** To use for simplifying... */
public class Simplifier extends CloneVisitor {
    public BinaryExpr visit(BinaryExpr n, Object arg){
        System.out.println ("Here:" + n.toString());
        return n;
    }
}