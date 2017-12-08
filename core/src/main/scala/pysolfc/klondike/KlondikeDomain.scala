package pysolfc.klondike

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Python
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{PythonSemanticTypes, constraintCodeGenerators}
import pysolfc.shared.GameTemplate

// domain
import domain._

/**
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class KlondikeDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with PythonSemanticTypes {

  /**
    * Convert ID into string. Each different variation adds a unique ID to the pygames grouping
   */
  @combinator object klondikeID extends IdForGame(pygames.klondike)

  @combinator object OutputFile {
    def apply: String = "klondike"
    val semanticType:Type = game(pysol.fileName)
  }

  /**
    * NO special constraints just yet
    */
  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Python] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Specialized methods to help out in processing constraints. Specifically,
    * these are meant to be generic, things like getTableua, getReserve()
    */
  @combinator object HelperMethodsCastle {
    def apply: Python = Python("")

    val semanticType: Type = constraints(constraints.methods)
  }

  @combinator object InitView {
    def apply(): Python = {

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)
      val waste = solitaire.containers.get(SolitaireContainerTypes.Waste)

      val sw:Python = layout_place_stock_and_waste(solitaire, stock, waste)

      // when placing a single element in Layout, use this API
      val fd:Python = layout_place_foundation(solitaire,found)
      val cs:Python = layout_place_tableau(solitaire,tableau)

      // Need way to simply concatenate Python blocks
      val comb = Python(sw.getCode.toString ++ cs.getCode.toString ++ fd.getCode.toString)
      comb
    }

    val

    semanticType: Type = game(game.view)
  }
}
