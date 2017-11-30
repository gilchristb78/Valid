package example.expression

import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import expression.Exp
import expression.Operation

/**
  * These codify the semantic types used by the Expression problem.
  */
trait SemanticTypes {
  // Abstract Visitor class to be generated.
  object generated {
    def apply (uniq:Type) : Constructor = 'Generated(uniq)

    val visitor: Type = 'Visitor
  }

  // Classes for each Exp subclass
  object exp {
    def apply (phase:Type, exp:Exp) : Constructor =  'Exp(phase, Constructor(exp.getClass.getSimpleName))

    val base:Type = 'Base           // initial class
    val visitor:Type = 'Visitor     // once visitor has been added
  }

  // Classes for each Op subclass
  object ops {
    def apply (phase:Type, op:Operation) : Constructor = 'Ops(phase, Constructor(op.getClass.getSimpleName))

    val base:Type = 'Base           // initial class
    val visitor:Type = 'Visitor     // once visitor has been added
  }
}
