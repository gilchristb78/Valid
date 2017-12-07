package org.combinators.solitaire.klondike

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, constraintCodeGenerators, generateHelper}

// domain
import domain._

/**
  * Define domain using Score52 since this is a single-deck solitaire game.
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class KlondikeDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with SemanticTypes
  with GameTemplate with Controller {

  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  @combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)
  @combinator object MakeWastePileView extends ExtendView("PileView", "WastePileView", "WastePile", 'WastePileViewClass)


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


  /** No helper methods for Klondike. */
  @combinator object HelperMethodsKlondike {
    def apply(): Seq[MethodDeclaration] = generateHelper.helpers(solitaire)

    val semanticType: Type = constraints(constraints.methods)
  }
}
