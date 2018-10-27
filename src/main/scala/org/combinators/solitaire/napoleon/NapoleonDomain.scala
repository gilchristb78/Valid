package org.combinators.solitaire.napoleon

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}
import org.combinators.templating.twirl.Java

/**
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class NapoleonDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with SemanticTypes
  with GameTemplate with Controller {

  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /** Each Solitaire variation must provide default do generation. */
  @combinator object DefaultDoGenerator {
    def apply: CodeGeneratorRegistry[Seq[Statement]] = constraintCodeGenerators.doGenerators

    val semanticType: Type = constraints(constraints.do_generator)
  }

  /** Each Solitaire variation must provide default conversion for moves. */
  @combinator object DefaultUndoGenerator {
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

      generateHelper.helpers(solitaire)
    }

    val semanticType: Type = constraints(constraints.methods)
  }
}
