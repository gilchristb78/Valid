package pysolfc.fan

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
class FanDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with PythonSemanticTypes {

  /**
    * Convert ID into string. Each different variation adds a unique ID to the pygames grouping
    */
  @combinator object fanID extends IdForGame(pygames.fan)

  @combinator object OutputFile {
    def apply: String = "fan"

    val semanticType: Type = game(pysol.fileName)
  }


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
    def apply: Python = {
      val helpers:Seq[Python] = Seq(generateHelper.tableau(), generateHelper.waste())

      Python(helpers.mkString("\n"))
    }

    val semanticType: Type = constraints(constraints.methods)
  }

  /*
  /**
    * Specialized methods to help out in processing constraints. Specifically,
    * these are meant to be generic, things like getTableua, getReserve()
    */
  @combinator object HelperMethodsCastle {
    def apply: Python = {
      val helpers:Seq[Python] = Seq(generateHelper.tableau(),

        Python(s"""
                  |def toLeftOf (targetCards, source):
                  |    for t in tableau():
                  |        if t == source:
                  |            return False
                  |        if t.cards == targetCards:
                  |            return True
                  |    return False
                  |
                  |def allSameRank():
                  |    top = tableau()[0].cards
                  |    if len(top) == 0:
                  |	    return False
                  |    for t in tableau():
                  |        next = t.cards[-1]
                  |        if next.rank != top[-1].rank:
                  |            return False
                  |    return True
                  |""".stripMargin)
      )

      Python(helpers.mkString("\n"))
    }

    val semanticType: Type = constraints(constraints.methods)
  }
  //
  //  @combinator object InitView {
  //    def apply(): Python = {
  //
  //      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
  //      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)
  //
  //      val sw:Python = layout_place_stock(solitaire, stock)
  //
  //      // when placing a single element in Layout, use this API
  //      val cs:Python = layout_place_tableau(solitaire, tableau)
  //
  //      // Need way to simply concatenate Python blocks
  //      val comb = Python(sw.getCode.toString ++ cs.getCode.toString)
  //      comb
  //    }
  //
  //    val
  //
  //    semanticType: Type = game(game.view)
  //  }
*/
}
