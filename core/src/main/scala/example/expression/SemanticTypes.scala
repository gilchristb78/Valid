package example.expression

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
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
  // http://i.cs.hku.hk/~bruno/papers/Modularity2016.pdf
  object ep {
    def apply (phase:Type, exp:Exp) : Constructor = 'Ep(phase, Constructor(exp.getClass.getSimpleName))
    def apply (phase:Type, op:Operation) : Constructor = 'Op(phase, Constructor(op.getClass.getSimpleName))
    def apply (phase:Type, exp:Exp, op:Operation) : Constructor = {
      val crossP = exp.getClass.getSimpleName + op.getClass.getSimpleName
      'Op(phase, Constructor(crossP))
    }

    val interface:Type       = 'Interface
    val defaultMethods:Type  = 'Default
    val finalType:Type       = 'Final
  }

  // For example, PrettyPrint + Collect -> architecture(PrettyP, Collect)
  object architecture {
    def apply (one:Exp, two:Exp)           : Constructor = 'Arch(Constructor(one.getClass.getSimpleName), Constructor(two.getClass.getSimpleName))
    def apply (head:Exp, tail:Constructor) : Constructor = 'Arch(Constructor(head.getClass.getSimpleName), tail)


  }

}
