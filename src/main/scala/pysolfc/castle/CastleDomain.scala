package pysolfc.castle

import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.solitaire.domain.{Constraint, MoveInformation, Solitaire}
import org.combinators.templating.twirl.Python
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{PythonSemanticTypes, constraintCodeGenerators}
import pysolfc.shared.GameTemplate

/**
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class CastleDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with PythonSemanticTypes {

  case class SufficientFree(src:MoveInformation, destination:MoveInformation, column:MoveInformation, tableau:MoveInformation) extends Constraint

  object castleCodeGenerator {
    val generators:CodeGeneratorRegistry[Python]= CodeGeneratorRegistry.merge[Python](

      CodeGeneratorRegistry[Python, SufficientFree] {
        case (registry:CodeGeneratorRegistry[Python], c:SufficientFree) =>
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          val column = registry(c.column).get
          val tableau = registry(c.tableau).get
          Python(s"""sufficientFree($column, $src, $destination, $tableau)""")

      },

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
              |def sufficientFree (column, src, destination, tableau):
              |    numEmpty = 0
              |    for s in tableau:
              |        if len(s.cards) == 0 and s != src and s != destination:
              |            numEmpty = numEmpty + 1
              |    return len(column) <= 1 + numEmpty
              |""".stripMargin)
      )

      Python(helpers.mkString("\n"))
    }

     val semanticType: Type = constraints(constraints.methods)
  }

  /**
    * Convert ID into string. Each different variation adds a unique ID to the pygames grouping
   */
  @combinator object castleID extends IdForGame(pygames.castle)

  @combinator object OutputFile {
    def apply: String = "castle"
    val semanticType:Type = game(pysol.fileName)
  }


//  @combinator object InitView {
//    def apply(): Python = {
//
//      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
//      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
//      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)
//
//      // start by constructing the DeckView
//      // If deck is invisible, then place invisibly
//      val dw:Python = if (!solitaire.isVisible(stock)) {    //         (stock.isInvisible) {
//        Python(s"""|
//                   |x, y = self.getInvisibleCoords()
//                   |s.talon = InitialDealTalonStack(x, y, self)
//                   |""".stripMargin)
//      } else {
//        layout_place_stock(solitaire,stock)
//      }
//
//      // when placing a single element in Layout, use this API
//      val fd:Python = layout_place_foundation(solitaire,found)
//      val cs:Python = layout_place_tableau(solitaire, tableau)
//
//      // Need way to simply concatenate Python blocks
//      val comb = Python(dw.getCode.toString ++ cs.getCode.toString ++ fd.getCode.toString)
//      comb
//    }
//
//    val
//
//    semanticType: Type = game(game.view)
//  }
}
