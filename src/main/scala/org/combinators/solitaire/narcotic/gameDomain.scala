package org.combinators.solitaire.narcotic

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

  object narcoticCodeGenerator {
    val generators:CodeGeneratorRegistry[Expression] = CodeGeneratorRegistry.merge[Expression](

      CodeGeneratorRegistry[Expression, ToLeftOf] {
        case (registry:CodeGeneratorRegistry[Expression], c:ToLeftOf) =>
          val destination = registry(c.destination).get
          val src = registry(c.src).get
          Java(s"""((org.combinators.solitaire.narcotic.Narcotic)game).toLeftOf($destination, $src)""").expression()

      },

      CodeGeneratorRegistry[Expression, AllSameRank] {
        case (registry:CodeGeneratorRegistry[Expression], all:AllSameRank) =>
          val inner = registry(all.src).get
          Java(s"""((org.combinators.solitaire.narcotic.Narcotic)game).allSameRank($inner)""").expression()

      }
    ).merge(constraintCodeGenerators.generators)
  }

  @combinator object NarcoticGenerator {
    def apply: CodeGeneratorRegistry[Expression] = {
      print ("In ngen")
      narcoticCodeGenerator.generators
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

  @combinator object HelperMethodsNarcotic {
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
      Java(s"""|public boolean toLeftOf(Stack target, Stack src) {
               |  // Check whether target is to left of src
               |  for (int i = 0; i < tableau.length; i++) {
               |    if (tableau[i] == target) {
               |      return true;   // found target first (in left-right)
               |    }
               |    if (tableau[i] == src) {
               |      return false;  // found src first
               |    }
               |  }
               |  return false; // will never get here
               |}
               |
                | public boolean allSameRank(Stack[] group) {
               |   if (group[0].empty()) { return false; }
               |   // Check whether tops of all piles are same rank
               |   for (int i = 1; i < group.length; i++) {
               |      if (group[i].empty()) { return false; }
               |      if (group[i].rank() != group[i-1].rank()) {
               |        return false;
               |      }
               |   }
               |  // looks good
               |  return true;
               |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

    }

    val semanticType: Type = game(game.methods)
  }

}
