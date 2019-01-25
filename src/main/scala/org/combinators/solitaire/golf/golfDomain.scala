package org.combinators.solitaire.golf

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

// Looks awkward how solitaire val is defined, but I think I need to do this
// to get the code to compile 
class golfDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {


  // override as needed in your own own specialized trait. I.e. "AcesUpPile" -> "PileView"
  override def baseViewNameFromElement (e:Element): String = {
    e match {
      case WastePile => "PileView"
      case _ => super.baseViewNameFromElement(e)
    }
  }
  // override as needed in your own own specialized trait. I.e. "AcesUpPile" -> "Pile"
  override def baseModelNameFromElement (e:Element): String = {
    e match {
      case WastePile => "Pile"
      case _ => super.baseViewNameFromElement(e)
    }
  }


  object golfCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression]().merge(constraintCodeGenerators.generators)
  }

  @combinator object golfGenerator {
    def apply: CodeGeneratorRegistry[Expression] = {
      print ("In ggen")
      golfCodeGenerator.generators
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

  @combinator object HelperMethodsGolf {
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
    def apply(): Seq[MethodDeclaration] = Seq.empty[MethodDeclaration]

    val semanticType: Type = game(game.methods)
  }
}
