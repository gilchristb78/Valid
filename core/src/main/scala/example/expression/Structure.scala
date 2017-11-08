package example.expression

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.{ConstructorDeclaration, FieldDeclaration, MethodDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import expression.{Attribute, DomainModel, Exp, Operation}

import scala.collection.JavaConverters._

trait Structure extends Base with SemanticTypes {

  /** Add dynamic combinators as needed. */
  override def init[G <: ExpressionDomain](gamma: ReflectedRepository[G], model: DomainModel): ReflectedRepository[G] = {
    var updated = super.init(gamma, model)

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



}


