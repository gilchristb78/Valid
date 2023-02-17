package org.combinators.solitaire.Egyptian

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

class gameDomain (override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with SemanticTypes
  with GameTemplate  with Controller {

  case class AllSameSuit(on: MoveInformation) extends Constraint

  // TODO: Should be able to derive this from the modeling
  override def baseModelNameFromElement (e:Element): String = {
    e match {
      case WastePile => "Pile"
      case _ => super.baseModelNameFromElement(e)
    }
  }

  override def baseViewNameFromElement (e:Element): String = {
    e match {
      case WastePile => "PileView"
      case _ => super.baseViewNameFromElement(e)
    }
  }

  object egyptianCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, AllSameSuit] {
        case (registry:CodeGeneratorRegistry[Expression], c:AllSameSuit) =>
          val column = registry(c.on).get
          Java(s"""ConstraintHelper.allSameSuit($column)""").expression()
      },

    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object egyptianGenerator {
    def apply: CodeGeneratorRegistry[Expression] = egyptianCodeGenerator.generators

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

  @combinator object HelperMethodsFreeCell {
    def apply(): Seq[BodyDeclaration[_]] = {
      val methods = generateHelper.helpers(solitaire)

      methods ++ Java(s"""
           |public static boolean allSameSuit (Stack st) {
           |  if (st.empty()) { return true; }
           |    	int n = st.count();
           |
           |    	Card c = st.peek(0);
           |    	for (int i = 1; i < n; i++) {
           |    		if (!c.sameSuit(st.peek(i))) {
           |    			return false;
           |    		}
           |    	}
           |  return true;
           |}""".stripMargin).methodDeclarations()
    }

    val semanticType: Type = constraints(constraints.methods)
  }

//  @combinator object MakeWastePile extends ExtendModel("Pile", "WastePile", 'WastePileClass)
//
//  @combinator object MakeWastePileView extends ExtendView("View", "WastePileView", "WastePile", 'WastePileViewClass)

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
              |public boolean validColumn(Column column) {
              |		return column.descending();
              |}
            """.stripMargin).methodDeclarations()
    }

    val semanticType: Type = game(game.methods)
  }

}

