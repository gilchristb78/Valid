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
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import com.github.javaparser.ast.body.BodyDeclaration
import scala.collection.mutable.ListBuffer
import org.combinators.generic
import org.combinators.cls.types.Kinding
import org.combinators.cls.types.Variable
import org.combinators.cls.types.{Taxonomy, Type}


// just to show that traits can pull in other combinators by extension
trait SomeCombinators extends generic.JavaCodeIdioms  {

  val alpha = Variable("TempType")

  // by naming as 'kinding' this val (which is inside of this trait)
  // is just a field. 
  val kindingSpecial = Kinding(alpha).
		   addOption('Celsius).
		   addOption('Fahrenheit).
		   addOption('Kelvin)

  val kindingAnother = Kinding(alpha).
		   addOption('RoomTemperature).
		   addOption('Boiling).
		   addOption('Freezing)

  // by making this a FIELD it will be detected and then accessible. Note
  // these params are strings but they will be referenced as 'Kelvin, i.e. 
  val taxonomySpecial = Taxonomy("Temperature").
		   addSubtype("Celsius").
		   addSubtype("Fahrenheit").
		   addSubtype("Kelvin")		
 
  val taxonomyAnother = Taxonomy("Unit").
		   addSubtype("Imperial").
		   addSubtype("Metric")
 
  // this is how you create a generic template for a combinator
  @combinator object UpInterface {

    def apply(exp:Expression): CompilationUnit = {
      val s = exp.toString
      Java(s"""|public class TemperatureAdapter {
               |  float getTemperature() {
               |    return $s;
               |  }
               |}""".stripMargin).compilationUnit()
    }

    val semanticType:Type = 'Float :&: alpha =>: 'TemperatureInterface (alpha)
  }
  /*****
  combinator object Bad {
    def apply() : Exp = {
      Java (s"""\"string\"""").expression()
    }

    val semanticType:Type = 'Float :&: 'Fahrenheit
  }
  ******/

  @combinator object ConvCelsiusToFahrenheit {

    def apply(e1:Expression):Expression = {
       val s = e1.toString
       Java("""((9.0/5.0)*""" + s + """ + 32.0)""").expression()
    }

    val semanticType:Type = 'Float :&: 'Celsius =>:
                            'Float :&: 'Fahrenheit

  }

  @combinator object SomeNumber {
    def apply(): Int = 42
    val semanticType = 'ReasonOfTheUniverse
  }

}


