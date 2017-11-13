package example.expression

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.{ConstructorDeclaration, FieldDeclaration, MethodDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Collect, Neg, PrettyP, Sub}
import expression.operations.SimplifyAdd
import expression._

import scala.collection.JavaConverters._

trait Structure extends Base with SemanticTypes {

  /** Add dynamic combinators as needed. */
  override def init[G <: ExpressionDomain](gamma: ReflectedRepository[G], model: DomainModel): ReflectedRepository[G] = {
    var updated = super.init(gamma, model)

    def registerImpl (op:Operation, map:Map[Exp,String]): Unit = {
      map.keys.foreach {
        key =>
          addImpl(op, key, Java(map(key)).statements())
      }
    }

    // implementations of operations: have to be defined before combinators?
    // consider codegeneratorregistry as before with constraints
    registerImpl(new Eval, Map(
      new Lit -> "return e.getValue();",
      new Add -> "return e.getLeft().accept(this) + e.getRight().accept(this);",
      new Sub -> "return e.getLeft().accept(this) - e.getRight().accept(this);",
      new Neg -> "return -e.getExp().accept(this);"
    ))

    registerImpl(new PrettyP, Map(
      new Lit -> """return "" + e.getValue();""",
      new Add -> """return "(" + e.getLeft().accept(this) + "+" + e.getRight().accept(this) + ")";""",
      new Sub -> """return "(" + e.getLeft().accept(this) + "-" + e.getRight().accept(this) + ")";""",
      new Neg -> """return "-" + e.getExp().accept(this);"""
    ))

    val combined:String =
      """
        |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
        |list.addAll(e.getLeft().accept(this));
        |list.addAll(e.getRight().accept(this));
        |return list;
      """.stripMargin

    registerImpl(new Collect, Map(
      new Lit -> """
                  |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
                  |list.add(e.getValue());
                  |return list;
                  """.stripMargin,
      new Add -> combined,
      new Sub -> combined,
      new Neg -> """
                   |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
                   |list.addAll(e.getExp().accept(this));
                   |return list;
                 """.stripMargin
    ))

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
    * @param sub    sub-type of Exp (i.e., Lit) for whom implementation class is synthesized.
    */
  class ImplClass(sub:Exp) {
    def apply(unit:CompilationUnit): CompilationUnit = {

      // Builds up the attribute fields and set/get methods. Also prepares for one-line constructor.
      var params:Seq[String] = Seq.empty
      var cons:Seq[String] = Seq.empty

      sub.ops.asScala.foreach {
        case att: Attribute =>
          val capAtt = att.attName.capitalize
          val tpe = Type_toString(att.attType)
          val fields:Seq[FieldDeclaration] = Java(s"""
                           |private $tpe ${att.attName};
                           |""".stripMargin).fieldDeclarations()
          fields.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

          // prepare for constructor
          params = params :+ s"$tpe ${att.attName}"
          cons   = cons   :+ s"  this.${att.attName} = ${att.attName};"

          // make the set/get methods
          val methods:Seq[MethodDeclaration] = Java(s"""
                       |public $tpe get$capAtt() { return ${att.attName};}
                       |public void set$capAtt($tpe val) { this.${att.attName} = val; }
                      """.stripMargin).methodDeclarations()

          methods.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

        case _ =>
      }

      // make constructor
      val constructor = Java(
        s"""
           |public ${sub.getClass.getSimpleName} (${params.mkString(",")}) {
           |   ${cons.mkString("\n")}
           |}""".stripMargin).constructors().head

      unit.getTypes.get(0).getMembers.add(constructor)

      val visitor = Java (
        s"""
           |public <R> R accept(Visitor<R> v) {
           |   return v.visit(this);
           |}
       """.stripMargin).methodDeclarations()

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
         |    System.out.println(e.accept(new Eval()));
         |    System.out.println(e.accept(new PrettyP()));
         |    e = new Add(new Add(new Lit(3), new Add(new Lit(5), new Sub(new Lit(3), new Lit(8)))), new Lit(-3));
         |    System.out.println(e.accept(new PrettyP()));
         |    System.out.println(e.accept(new Eval()));
         |    java.util.List<Integer> lits = e.accept(new Collect());
         |    System.out.println(lits);
         |  }
         |}""".stripMargin).compilationUnit()

    val semanticType:Type = driver
  }

}


