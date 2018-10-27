package pysolfc.freecell

import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Python
import domain.freeCell.SufficientFree
import org.combinators.solitaire.domain.Solitaire
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{PythonSemanticTypes, constraintCodeGenerators}
import pysolfc.shared.GameTemplate

/**
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class FreeCellDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with PythonSemanticTypes {

  /**
    * Convert ID into string. Each different variation adds a unique ID to the pygames grouping
   */
  @combinator object freeCellID extends IdForGame(pygames.freecell)

  @combinator object OutputFile {
    def apply: String = "freecell"
    val semanticType:Type = game(pysol.fileName)
  }

  object freeCellCodeGenerator {
    val generators:CodeGeneratorRegistry[Python] = CodeGeneratorRegistry.merge[Python](

      CodeGeneratorRegistry[Python, SufficientFree] {
        case (registry:CodeGeneratorRegistry[Python], c:SufficientFree) =>
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          Python(s"""sufficientFree($destination, $src)""")

      },

    ).merge(constraintCodeGenerators.generators)
  }

  /**
    * Castle requires specialized extensions for constraints to work.
    */
  @combinator object FreeCellGenerator {
    def apply: CodeGeneratorRegistry[Python] = {
      freeCellCodeGenerator.generators
    }
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
    def apply: Python = {
      val helpers:Seq[Python] = Seq(generateHelper.tableau(),

        Python(s"""
                  |def sufficientFree (destination, source):
                  |    return True
                  |
                  |""".stripMargin)
      )

      Python(helpers.mkString("\n"))
    }

    val semanticType: Type = constraints(constraints.methods)
  }

}
