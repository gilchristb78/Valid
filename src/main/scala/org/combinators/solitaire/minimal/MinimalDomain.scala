package org.combinators.solitaire.minimal

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
class MinimalDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with SemanticTypes
  with GameTemplate with Controller {

  val customRegistry =  CodeGeneratorRegistry[Expression]
    .addGenerator[AndConstraint] (
    (registry:CodeGeneratorRegistry[Expression],
     and: AndConstraint) =>
      if (and.args.isEmpty) {
        Java("true").expression()
      } else {
        and.args.tail.foldLeft(
          registry(and.args.head).get) {
          case (s, c) =>
            val inner = registry(c)
            if (inner.isEmpty) {
              Java(s"${c.toString}").expression()
            } else {
              Java(s"($s && ${inner.get})").expression()
            }
        }
      }
    ).addGenerator[IsAce] (
      (registry: CodeGeneratorRegistry[Expression],
       isAce: IsAce) => {
        val e = registry(isAce.on).get
        Java(s"$e.getRank() == Card.ACE").expression() }
    ).addGenerator[IsEmpty] (
      (registry: CodeGeneratorRegistry[Expression],
       isEmpty:IsEmpty) => {
        val e = registry(isEmpty.on).get
        Java(s"$e.empty()").expression() }
    )

  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Expression] = customRegistry   // constraintCodeGenerators.generators
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

  @combinator object HelperMethodsArchway {
    def apply(): Seq[BodyDeclaration[_]] = generateHelper.helpers(solitaire)

    val semanticType: Type = constraints(constraints.methods)
  }

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

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty

    val semanticType: Type = game(game.methods)
  }

  @combinator object HelperMethodsMinimal {
    def apply(): Seq[BodyDeclaration[_]] = Seq.empty

    val semanticType: Type = constraints(constraints.methods)
  }
}
