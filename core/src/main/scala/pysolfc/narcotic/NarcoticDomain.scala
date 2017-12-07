package pysolfc.narcotic

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Python
import domain.narcotic.{AllSameRank, ToLeftOf}
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{PythonSemanticTypes, constraintCodeGenerators}
import pysolfc.shared.GameTemplate

// domain
import domain._

/**
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class NarcoticDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with PythonSemanticTypes {

  /**
    * Convert ID into string. Each different variation adds a unique ID to the pygames grouping
   */
  @combinator object narcoticID extends IdForGame(pygames.klondike)

  @combinator object OutputFile {
    def apply: String = "narcotic"
    val semanticType:Type = game(pysol.fileName)
  }

  object castleCodeGenerator {
    val generators:CodeGeneratorRegistry[Python] = CodeGeneratorRegistry.merge[Python](

      CodeGeneratorRegistry[Python, ToLeftOf] {
        case (registry:CodeGeneratorRegistry[Python], c:ToLeftOf) =>
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          Python(s"""toLeftOf($destination, $src)""")

      },

      CodeGeneratorRegistry[Python, AllSameRank] {
        case (_:CodeGeneratorRegistry[Python], _:AllSameRank) =>
          Python(s"""allSameRank()""")
      }

    ).merge(constraintCodeGenerators.generators)
  }

  /**
    * Castle requires specialized extensions for constraints to work.
    */
  @combinator object CastleGenerator {
    def apply: CodeGeneratorRegistry[Python] = {
      castleCodeGenerator.generators
    }
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Specialized methods to help out in processing constraints. Specifically,
    * these are meant to be generic, things like getTableua, getReserve()
    */
  @combinator object HelperMethodsCastle {
    def apply: Python = {
      val helpers:Seq[Python] = Seq(generateHelper.tableau(),

        Python(s"""
                  |def toLeftOf (destination, source):
                  |    return False
                  |
                  |def allSameRank():
                  |    return False
                  |""".stripMargin)
      )

      Python(helpers.mkString("\n"))
    }

    val semanticType: Type = constraints(constraints.methods)
  }

  @combinator object InitView {
    def apply(): Python = {

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)

      val sw:Python = layout_place_stock(stock)

      // when placing a single element in Layout, use this API
      val cs:Python = layout_place_tableau(tableau)

      // Need way to simply concatenate Python blocks
      val comb = Python(sw.getCode.toString ++ cs.getCode.toString)
      comb
    }

    val

    semanticType: Type = game(game.view)
  }
}
