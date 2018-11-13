package org.combinators.solitaire.TEMPLATE

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
class gameDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {

  object templateCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, ToLeftOf] {
        case (registry:CodeGeneratorRegistry[Expression], c:ToLeftOf) =>
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          Java(s"""((org.combinators.solitaire.tEMPLATE.TEMPLATE)game).toLeftOf($destination, $src)""").expression()

      },

      CodeGeneratorRegistry[Expression, AllSameRank] {
        case (registry:CodeGeneratorRegistry[Expression], all:AllSameRank) =>
          val inner = registry(all.src).get
          Java(s"""((org.combinators.solitaire.tEMPLATE.TEMPLATE)game).allSameRank($inner)""").expression()

      }
    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object TEMPLATEGenerator {
    def apply: CodeGeneratorRegistry[Expression] = {
      print ("In ngen")
      tEMPLATECodeGenerator.generators
    }
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  @combinator object HelperMethodsNtEMPLATE {
    def apply(): Seq[BodyDeclaration[_]] = generateHelper.helpers(solitaire)

    val semanticType: Type = constraints(constraints.methods)
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
    def apply(): Seq[MethodDeclaration] = {
    }

    val semanticType: Type = game(game.methods)
  }

}
