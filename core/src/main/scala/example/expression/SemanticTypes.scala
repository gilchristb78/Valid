package example.expression

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.SimpleName
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import expression.Exp
import expression.Operation
import expression.types.{FrameworkTypes, GenericType, TypeInformation, Types}


/**
  * These codify the semantic types used by the Expression problem.
  *
  * For any of these that are ever going to be translated directly into Java Type Names, you must
  * make them Constructor.
  */
trait SemanticTypes {

  /** Implementations for an operation. Map(op, Map(exp,MethodDecls)). */
  var implementations:Map[Class[_ <: Operation],Map[Class[_ <: Exp],MethodDeclaration]] = Map()

  /**
    * Return desired map of expressions by operation.
    *
    * @param op
    * @return
    */
  def getImplementation(op:Operation):Map[Class[_ <: Exp],MethodDeclaration] = implementations(op.getClass)

  /** Convert a type into its Java String equivalent. */
  def Type_toString (ty:TypeInformation): String =
    ty match {
      case Types.Exp=> "Exp"           // base class of everything

      case Types.Void => "void"
      case Types.Int => "Integer"      // allow boxing/unboxing for generics
      case Types.String => "String"
      case g:GenericType => Type_toString(g.base) + "<" + Type_toString(g.generic) + ">"
      case FrameworkTypes.List => "java.util.List"
      case _ => "None"
    }

  def driver:Type = 'Driver

  // meta-concerns. When you have completed the definition of a constructor
  object generated {
    def apply (uniq:Type) : Constructor = 'Generated(uniq)

    val visitor: Type = 'Visitor
    val complete: Type = 'Complete
    val initialized: Type = 'Initialized
  }

  object exp {
    def apply (phase:Type, exp:Exp) : Constructor =  'Exp(phase, Constructor(exp.getClass.getSimpleName))

    val base:Type = 'Base           // initial class
    val visitor:Type = 'Visitor     // once visitor has been added

  }

  /**
    * Each operation has its own type, assuming operation names are valid Java SimpleNames.
    */
  object ops {
    def apply (phase:Type, op:Operation) : Constructor = 'Ops(phase, Constructor(op.getClass.getSimpleName))

    val base:Type = 'Base           // initial class
    val visitor:Type = 'Visitor     // once visitor has been added
  }

  /**
    * Types appear here
    */
  object data {
    def apply (uniq:SimpleName) : Constructor = 'Data(Constructor(uniq.toString))
  }

  // common structures

}
