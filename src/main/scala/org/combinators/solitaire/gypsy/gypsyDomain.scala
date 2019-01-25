package org.combinators.solitaire.gypsy

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.nomad
import org.combinators.solitaire.milligancell


class gypsyDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {

  override def baseModelNameFromElement (e:Element): String = {
    e match {
      case nomad.FreeCell => "Pile"
      case milligancell.FreeCell => "Pile"
      case _ => super.baseModelNameFromElement(e)
    }
  }

  override def baseViewNameFromElement (e:Element): String = {
    e match {
      case nomad.FreeCell => "PileView"
      case milligancell.FreeCell => "PileView"
      case _ => super.baseViewNameFromElement(e)
    }
  }

  object gypsyCodeGenerator {
    def generators(pkg:Name, varName:SimpleName):CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, AllSameSuit] {
        case (registry:CodeGeneratorRegistry[Expression], c:AllSameSuit) =>
          val moving = registry(c.movingCards).get
          Java(s"(($pkg.$varName)game).allSameSuit($moving)").expression()
      }
    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object GypsyGenerator {
    def apply(pkg:Name, varName:SimpleName): CodeGeneratorRegistry[Expression] = {
      gypsyCodeGenerator.generators(pkg, varName)
    }
    val semanticType: Type =   packageName =>: variationName =>:
      constraints(constraints.generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  @combinator object HelperMethodsGypsy {
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
      Java(s"""|public boolean allSameSuit(Stack col) {
               | if(col.empty() || col.count() == 1) { return true; }
               | else{
               |   Card c1, c2;
               |   int size = col.count();
               |   for(int i = 1; i < size; i++){
               |    c1 = col.peek(i - 1);
               |    c2 = col.peek(i);
               |    if(c1.getSuit() != c2.getSuit()) { return false; }
               |   }
               |   return true;
               | }
               |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

    }

    val semanticType: Type = game(game.methods)
  }

}