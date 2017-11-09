package example.expression

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.twirl.Java
import expression.data.{Add, Eval, Lit}
import expression._
import expression.extensions.{Neg, PrettyP, Sub}
import expression.operations.SimplifyAdd

import scala.collection.JavaConverters._

/** Future work to sanitize combinators to be independent of Exp. */
class ExpressionSynthesis(override val domain:DomainModel) extends ExpressionDomain(domain) with SemanticTypes {


  /**
    * For the given operation, add the sequence of statements to implement for given expression subtype.
    * This dynamically maintains a map which can be inspected for the code synthesis.
    *
    * @param op
    * @param exp
    * @param stmts
    */
  def addImpl(op:Operation, exp:Exp, stmts:Seq[Statement]): Unit = {
    val name = exp.getClass.getSimpleName

    var map:Map[Class[_ <: Exp],MethodDeclaration] = if (implementations.contains(op.getClass)) {
      implementations(op.getClass) - exp.getClass
    } else {
      Map()
    }

    val tpe:String = Type_toString(op.`type`)
    map += (exp.getClass -> Java(
      s"""
         |public $tpe visit($name e) {
         |   ${stmts.mkString("\n")}
         |}
        """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration]).head)

    implementations -= op.getClass
    implementations += (op.getClass -> map)
  }

  /** Construct visitor abstract class. */
  @combinator object Visitor {
    def apply(): CompilationUnit = {
      val signatures = domain.data.asScala
            .map(x => s"""public abstract R visit(${x.getClass.getSimpleName} exp);""").mkString("\n")

      Java (s"""
           |package expression;
           |/*
           | * A concrete visitor describes a concrete operation on expressions. There is one visit
           | * method per type in the class hierarchy.
           | */
           |public abstract class Visitor<R> {
           |
           |$signatures
           |}
         """.stripMargin).compilationUnit()
    }

    val semanticType:Type = generated(generated.visitor)
  }


  /** Generate from domain. USER NEEDS TO SPECIFY THESE EITHER AUTOMATICALLY OR MANUALLY */
  @combinator object BaseExpClass {
    def apply() : CompilationUnit =
    Java(s"""
         |package expression;
         |
         |public abstract class Exp {
         |    public abstract <R> R accept(Visitor<R> v);
         |}
         |""".stripMargin).compilationUnit()

    val semanticType:Type = exp(exp.base, new Exp)
  }

  // implementations of operations: have to be defined before combinators?
  addImpl(new Eval, new Lit, Java(s"""return e.getValue();""").statements())
  addImpl(new Eval, new Neg, Java(s"""return -e.getExp().accept(this);""").statements())
  addImpl(new Eval, new Add, Java(s"""return e.getLeft().accept(this) + e.getRight().accept(this);""").statements())
  addImpl(new Eval, new Sub, Java(s"""return e.getLeft().accept(this) - e.getRight().accept(this);""").statements())

  addImpl(new PrettyP, new Lit, Java(s"""return "" + e.getValue();""").statements())
  addImpl(new PrettyP, new Neg, Java(s"""return "-" + e.getExp().accept(this);""").statements())
  addImpl(new PrettyP, new Add, Java(s"""return "(" + e.getLeft().accept(this) + "+" + e.getRight().accept(this) + ")";""").statements())
  addImpl(new PrettyP, new Sub, Java(s"""return "(" + e.getLeft().accept(this) + "-" +  e.getRight().accept(this) + ")";""").statements())

  addImpl(new SimplifyAdd, new Lit, Java(s"""return e;""").statements())   // nothing to simplify.
  addImpl(new SimplifyAdd, new Neg, Java(
        s"""
           |if (e.getExp().accept(new Eval()) == 0) {
           |  return new Lit(0);
           |} else {
           |  return e;
           |}
         """.stripMargin).statements())

  addImpl(new SimplifyAdd, new Sub, Java(
        s"""
           |if (e.getLeft().accept(new Eval()) == e.getRight().accept(new Eval())) {
           |  return new Lit(0);
           |} else {
           |  return new Sub(e.getLeft().accept(this), e.getRight().accept(this));
           |}
           |""".stripMargin).statements())

  addImpl(new SimplifyAdd, new Add, Java(
    s"""
       |int leftVal = e.getLeft().accept(new Eval());
       |int rightVal = e.getRight().accept(new Eval());
       |if ((leftVal == 0 && rightVal == 0) || (leftVal + rightVal == 0)) {
       |  return new Lit(0);
       |} else if (leftVal == 0) {
       |  return e.getRight().accept(this);
       |} else if (rightVal == 0) {
       |  return e.getLeft().accept(this);
       |} else {
       |  return new Add(e.getLeft().accept(this), e.getRight().accept(this));
       |}
       |""".stripMargin).statements())


  addImpl(new PrettyP, new Neg, Java(s"""return "-" + e.getExp().accept(this);""").statements())
  addImpl(new PrettyP, new Add, Java(s"""return "(" + e.getLeft().accept(this) + "+" + e.getRight().accept(this) + ")";""").statements())
  addImpl(new PrettyP, new Sub, Java(s"""return "(" + e.getLeft().accept(this) + "-" +  e.getRight().accept(this) + ")";""").statements())

  // sample Driver
  @combinator object Driver {
    def apply:CompilationUnit = Java(
      s"""
         |package expression;
         |
         |public class Driver {
         |	public static void main(String[] args) {
         |		Exp e = new Add(new Add(new Lit(3), new Lit(9)), new Sub(new Lit(13), new Lit(5)));
         |
         |    System.out.println(e.accept(new Eval()));
         |    System.out.println(e.accept(new PrettyP()));
         |
         |    e = new Add(new Add(new Lit(3), new Add(new Lit(5), new Sub (new Lit(3), new Lit(8)))), new Lit(-3));
         |    System.out.println(e.accept(new Eval()));
         |    System.out.println(e.accept(new PrettyP()));
         |
         |    Exp f = e.accept(new SimplifyAdd());
         |    System.out.println(f.accept(new Eval()));
         |    System.out.println(f.accept(new PrettyP()));
         |  }
         |}""".stripMargin).compilationUnit()

    val semanticType:Type = driver
  }
}
