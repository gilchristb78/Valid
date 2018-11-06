package org.combinators.solitaire.spider

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.stmt.Statement
//import domain.spider.AllSameSuit what to do with this?? put it in solitaire.domain?
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}


/**
  * Defines Java package, the game's name, initializes the domain model,
  * the UI, and the controllers (doesn't define them, just generates),
  * and includes extra fields and methods.
  */
//removed "with semantic types"
//changed c.base to c.src for allsamesuit
class SpiderDomain(override val solitaire: Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {

  object SpiderCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, AllSameSuit] {
        case (registry:CodeGeneratorRegistry[Expression], c:AllSameSuit) =>
          val column = registry(c.src).get
          //Java(s"""ConstraintHelper.allSameSuit($column)""").expression()
          Java(s"""((org.combinators.solitaire.spider.Spider)game.allSameSuit($column)""").expression()
      },

    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object SpiderGenerator {
    def apply: CodeGeneratorRegistry[Expression] = SpiderCodeGenerator.generators

    val semanticType: Type = constraints(constraints.generator)
  }
/*
  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
  }
*/

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

  @combinator object HelperMethodsSpider {
    def apply(): Seq[BodyDeclaration[_]] = {
      val methods = generateHelper.helpers(solitaire)
      methods ++ Java(s"""
           |public static boolean allSameSuit (Column column) {
           | if(column.empty() || (column.count() == 1)) { return true; }
           | else{
           |  Card c1, c2;
           |  int size = column.count();
           |  for(int i = 1; i < size; i++){
           |   c1 = column.peek(i - 1);
           |   c2 = column.peek(i);
           |   if(c1.getSuit() != c2.getSuit()) { return false; }
           |  }
           |  return true;
           | }
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

      Java(s"""
         |public java.util.Enumeration<Move> availableMoves() {
         |    java.util.Vector<Move> v = new java.util.Vector<Move>();
         |
         |	  if (!this.deck.empty()) {
         |	    DealDeck dd = new DealDeck(deck, tableau);
         |		  if (dd.valid(this)) {
         |			  v.add(dd);
         |		  }
         |		}
         |    return v.elements();
         |}
       """.stripMargin).methodDeclarations()

    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }

}
