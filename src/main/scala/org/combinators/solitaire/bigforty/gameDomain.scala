package org.combinators.solitaire.bigforty
import domain._
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body. MethodDeclaration
import com.github.javaparser.ast.expr.{Expression, Name}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import domain.bigforty.AllSameSuit
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}

class gameDomain (override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with SemanticTypes
  with GameTemplate  with Controller {

  object bigFortyCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, AllSameSuit] {
        case (registry:CodeGeneratorRegistry[Expression], c:AllSameSuit) =>
          val column = registry(c.base).get
          Java(s"""ConstraintHelper.allSameSuit($column)""").expression()
      },

    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object bigFortyGenerator {
    def apply: CodeGeneratorRegistry[Expression] = bigFortyCodeGenerator.generators

    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  @combinator object HelperMethodsFreeCell {
    def apply(): Seq[MethodDeclaration] = {
      val methods = generateHelper.helpers(solitaire)

      methods ++ Java(s"""
           |public static boolean allSameSuit (Column column) {
           |  return true;
           |}""".stripMargin).methodDeclarations()
    }

    val semanticType: Type = constraints(constraints.methods)
  }

  @combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)

  @combinator object MakeWastePileView extends ExtendView("View", "WastePileView", "WastePile", 'WastePileViewClass)

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
    def apply(): Seq[MethodDeclaration] = {Java(s"""
              |public Dimension getPreferredSize() {
              |  return new Dimension (940, 600);
              |}
              |
              |public boolean validColumn(Column column) {
              |		return column.descending();
              |}
            """.stripMargin).methodDeclarations()
    }

    val semanticType: Type = game(game.methods)
  }

}

