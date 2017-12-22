package example
import com.github.javaparser.ast.CompilationUnit

// name clash
import com.github.javaparser.ast.`type`.{Type => JType}

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import com.github.javaparser.ast.body.BodyDeclaration
import org.combinators.generic
import _root_.java.util.UUID
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
import scala.collection.mutable.ListBuffer

trait OtherCombinators {

  @combinator object TemperatureAPI {
    def apply:Expression = {
      Java(s"""Temperature.getCurrentTemperature()""").expression()
    }

    val semanticType:Type = 'Float :&: 'Celsius
  }

}


