package org.combinators.solitaire.delta

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class DeltaDomain(override val solitaire: Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {

  object deltaCodeGenerator {
    val generators: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.generators
  }

  @combinator object deltaGenerator {
    def apply: CodeGeneratorRegistry[Expression] = deltaCodeGenerator.generators


    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators

    val semanticType: Type = constraints(constraints.map)
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

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty

    val semanticType: Type = game(game.methods)
  }

  @combinator object HelperMethods {
    def apply(): Seq[BodyDeclaration[_]] = generateHelper.helpers(solitaire)

    val semanticType: Type = constraints(constraints.methods)
  }

}
