package pysolfc.archway

import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Python
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{PythonSemanticTypes, constraintCodeGenerators}
import pysolfc.shared.GameTemplate

/**
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class ArchwayDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with PythonSemanticTypes {

  /**
    * Convert ID into string. Each different variation adds a unique ID to the pygames grouping
    */
  @combinator object castleID extends IdForGame(pygames.archway)

  /**
    * NO special constraints just yet
    */
  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Python] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Python] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }



  /**
    * Specialized methods to help out in processing constraints. Specifically,
    * these are meant to be generic, things like getTableua, getReserve()
    */
  @combinator object HelperMethodsCastle {
    def apply: Python = Python("")

     val semanticType: Type = constraints(constraints.methods)
  }


  @combinator object OutputFile {
    def apply: String = "archway"
    val semanticType:Type = game(pysol.fileName)
  }


}
