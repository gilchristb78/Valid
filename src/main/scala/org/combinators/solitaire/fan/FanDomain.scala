package org.combinators.solitaire.fan

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
import domain._
import domain.constraints.MaxSizeConstraint
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}
import org.combinators.templating.twirl.Java

/**
  * Defines Java package, the game's name, initializes the domain model,
  * the UI, and the controllers (doesn't define them, just generates),
  * and includes extra fields and methods.
  */
class FanDomain(override val solitaire: Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {

  object fanCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, MaxSizeConstraint] {
        case (registry:CodeGeneratorRegistry[Expression], c:MaxSizeConstraint) =>
          val destination = registry(c.destination).get
          val moving = registry(c.movingCards).get
          val num = c.maxSize
          Java(s"""ConstraintHelper.maxSizeExceeded($moving, $destination, $num)""").expression()
      },

    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object FanGenerator {
    def apply: CodeGeneratorRegistry[Expression] = fanCodeGenerator.generators
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

  @combinator object HelperMethodsFan {
    def apply(): Seq[BodyDeclaration[_]] = {
      val methods = generateHelper.helpers(solitaire)

      methods ++ Java(s"""
                         |public static boolean maxSizeExceeded(Card moving, Stack destination, int max) {
                         |    	return false;
                         |}""".stripMargin).methodDeclarations()
    }

    val semanticType: Type = constraints(constraints.methods)
  }


  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }

  /**
    * Generates import statements for the model and controller packages.
    */
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
    * Generate extra methods.
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] =

      Java(s"""public java.util.Enumeration<Move> availableMoves() {
              |  java.util.Vector<Move> v = new java.util.Vector<Move>();
              |        for (Column c : tableau) {
              |            for (Pile p : foundation) {
              |                PotentialMoveCardFoundation pfm = new PotentialMoveCardFoundation(c, p);
              |                if (pfm.valid(this)) {
              |                    v.add(pfm);
              |                }
              |            }
              |        }
              |        if (v.isEmpty()) {
              |            for (Column c : tableau) {
              |
              |            for (Column c2 : tableau) {
              |                PotentialMoveCard pm = new PotentialMoveCard(c, c2);
              |                if (pm.valid(this)) {
              |                    v.add(pm);
              |                }
              |            }
              |           }
              |        }
              |        return v.elements();
              |}
       """.stripMargin).methodDeclarations()

    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }

}
