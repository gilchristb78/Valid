package example.expression

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.{ConstructorDeclaration, FieldDeclaration, MethodDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Neg, PrettyP, Sub}
import expression.operations.SimplifyAdd
import expression.{Attribute, DomainModel, Exp, Operation}

import scala.collection.JavaConverters._

trait Structure extends Base with SemanticTypes {

  /** Add dynamic combinators as needed. */
  override def init[G <: ExpressionDomain](gamma: ReflectedRepository[G], model: DomainModel): ReflectedRepository[G] = {
    var updated = super.init(gamma, model)

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


    // Add relevant combinators to construct the sub-type classes, based on domain model.
      model.data.asScala.foreach {
        sub:Exp => {
          updated = updated
            .addCombinator (new BaseClass(sub))
            .addCombinator (new ImplClass(sub))
        }
      }

    model.ops.asScala.foreach {
      op:Operation => {
        updated = updated
          .addCombinator (new OpImpl(op))
      }
    }

//
//    // Desired operations
//    @combinator object EvalOp extends OpImpl(new Eval)
//    @combinator object PrettypOp extends OpImpl(new PrettyP)
//    @combinator object SimplifyAdd extends OpImpl(new SimplifyAdd)
//

    updated
  }

  /** Works on any subclass of Exp to produce the base class structure for a sub-type of Exp. */
  class BaseClass(expr:Exp) {
    def apply(): CompilationUnit = {

      val name = expr.getClass.getSimpleName

      Java(s"""package expression; public class $name extends Exp { }""".stripMargin).compilationUnit()
    }

    // semantic type is based on the subclass (i.e., it will be exp('Base, 'Lit) or exp('Base, 'Add)
    val semanticType:Type = exp(exp.base, expr)
  }

  /**
    * Construct class to represent subclass of Exp.
    *
    * @param sub
    */
  class ImplClass(sub:Exp) {
    def apply(unit:CompilationUnit): CompilationUnit = {

      // Builds up the attribute fields and set/get methods. Also prepares for one-line constructor.
      var params:Seq[String] = Seq.empty
      var cons:Seq[String] = Seq.empty

      sub.ops.asScala.foreach {
        case att: Attribute => {
          val capAtt = att.attName.capitalize
          val tpe = Type_toString(att.attType)
          val fields:Seq[FieldDeclaration] = Java(s"""
                                                     |private $tpe ${att.attName};
                                                     |""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
          fields.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

          // prepare for constructor
          params = params :+ s"$tpe ${att.attName}"
          cons   = cons   :+ s"  this.${att.attName} = ${att.attName};"

          // make the set/get methods
          val methods:Seq[MethodDeclaration] = Java(s"""
                                                       |public $tpe get$capAtt() { return ${att.attName};}
                                                       |public void set$capAtt($tpe val) { this.${att.attName} = val; }
                    """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

          methods.foreach { x => unit.getTypes.get(0).getMembers.add(x) }
        }
        case _ => {}
      }

      // make constructor
      val constructor = Java(
        s"""
           |public ${sub.getClass.getSimpleName} (${params.mkString(",")}) {
           |   ${cons.mkString("\n")}
           |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[ConstructorDeclaration]).head

      unit.getTypes.get(0).getMembers.add(constructor)

      val visitor = Java (
        s"""
           |public <R> R accept(Visitor<R> v) {
           |   return v.visit(this);
           |}
       """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

      visitor.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

      unit
    }

    val semanticType:Type = exp(exp.base, sub) =>: exp(exp.visitor,sub)
  }

  /** Brings in classes for each operation. These can only be completed with the implementations. */
  class OpImpl(op:Operation) {
    def apply: CompilationUnit = {

      val name = op.getClass.getSimpleName
      val tpe = Type_toString(op.`type`)

      //implementations
      val methods:Map[Class[_ <: Exp],MethodDeclaration] = getImplementation(op)

      val mds:Iterable[MethodDeclaration] = methods.values
      val signatures = mds.mkString("\n")

      val s = Java(
        s"""
           |package expression;
           |
           |public class $name extends Visitor<$tpe> {
           |
           |$signatures
           |
           |}
         """.stripMargin)

      s.compilationUnit()
    }

    val semanticType:Type = ops (ops.visitor,op)
  }

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


