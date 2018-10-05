package org.combinators.solitaire.klondike

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
import domain.klondike.RedealsAllowed
import domain.moves.ResetDeckMove
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain.Solitaire
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}

/**
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class KlondikeDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with SemanticTypes
  with GameTemplate with Controller {

  /**
    * Klondike needs ability to have constraint for move.
    *
    * This method appears in the extra methods [[HelperMethodsKlondike]] below.
    */
  object klondikeCodeGenerators {
    val generators:CodeGeneratorRegistry[Expression] =
      constraintCodeGenerators.generators.addGenerator (
         (registry:CodeGeneratorRegistry[Expression], ra:RedealsAllowed) =>
          Java(s"""ConstraintHelper.redealsAllowed()""")
            .expression[Expression]()
      )
  }

  /**
    * Combinator to identify the constraint generator to use when generating moves for Klondike.
    */
  @combinator object KlondikeGenerator {
    def apply: CodeGeneratorRegistry[Expression] = klondikeCodeGenerators.generators

    val semanticType: Type = constraints(constraints.generator)
  }

  /** Each Solitaire variation must provide default conversion for moves. */
  object limitedDealResets {
    val generators: CodeGeneratorRegistry[Seq[Statement]] = CodeGeneratorRegistry.merge[Seq[Statement]](

      CodeGeneratorRegistry[Seq[Statement], ResetDeckMove] {

        // retrieve old statements, and just add one more
        case (registry: CodeGeneratorRegistry[Seq[Statement]], r: ResetDeckMove) =>
          val old:Seq[Statement] = constraintCodeGenerators.doGenerators(r).get

          old ++ Java(s"""ConstraintHelper.takeRedeal();""").statements()
      },

    ).merge(constraintCodeGenerators.doGenerators)
  }

  /** In Klondike, we need to merge in code to handle the counting of decks being reset. */
  @combinator object KlondikeDoGenerator {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = limitedDealResets.generators

    val semanticType: Type = constraints(constraints.do_generator)
  }

  /** Each Solitaire variation must provide default conversion for moves. */
  @combinator object defaultUndoMoves {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = constraintCodeGenerators.undoGenerators

    val semanticType: Type = constraints(constraints.undo_generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  // vagaries of java imports means these must be defined as well.
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = packageName =>: game(game.imports)
  }

  /**
    * Contains the logic whether the given column is alternating colors and descending.
    * Useful for filtering the valid moves when pressing on a column.
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty

    val semanticType: Type = game(game.methods)
  }

  /**
    * Provide ConstraintHelper.takeRedeal() as method to invoke once deal happens.
    *   Have this be invoked from the doGenerator extension (which is yet to be written).
    *
    * Provide ConstraintHelper.redealsAllowed() to check if more redeals are allowed.
    */
  @combinator object HelperMethodsKlondike {
    def apply(): Seq[BodyDeclaration[_]] = {
      val max = 1 // TODO: FIXME solitaire.asInstanceOf[klondike.KlondikeDomain].numRedeals()

      generateHelper.helpers(solitaire) ++
        Java(s"""static int numDeals = $max;
                |public static void takeRedeal() { numDeals--; }
                |// if undo were allowed, we could have undoRedeal() method...
                |public static boolean redealsAllowed() { return (numDeals != 0); }""".stripMargin).classBodyDeclarations()
    }

    val semanticType: Type = constraints(constraints.methods)
  }
}
