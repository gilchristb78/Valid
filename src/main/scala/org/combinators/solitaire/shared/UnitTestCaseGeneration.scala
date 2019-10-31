package org.combinators.solitaire.shared
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.solitaire.shared
import _root_.java.util.UUID

import akka.actor.ActorSystem
import akka.event.Logging
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.SimpleName
import org.combinators.cls.types.{Constructor, Type}
import org.combinators.generic
import org.combinators.solitaire.domain._
import org.combinators.solitaire.shared.compilation._
import org.combinators.templating.twirl.Java
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.shared.cls.Synthesizer.complete


trait UnitTestCaseGeneration extends Base with shared.Moves with generic.JavaCodeIdioms with SemanticTypes {

  // generative classes for each of the required elements. This seems much more generic than worth being buried here.
  // PLEASE SIMPLIFY. TODO
  // HACK
  class SolitaireTestSuite(solitaire:Solitaire) {
    def apply(): CompilationUnit = {
      val pkgName = solitaire.name;
      val name = solitaire.name.capitalize;

      var methods:Seq[MethodDeclaration] = Seq.empty
      for (m <- solitaire.moves) {
        val sym = Constructor(m.name)
        val method = Java(
          s"""
             |@Test
             |public void test${m.name} () {
             |
             |}""".stripMargin).methodDeclarations().head

        methods = methods :+ method
      }

      val container = Java(s"""|package org.combinators.solitaire.${pkgName};
               |public class ${name}TestCases {
               |
               |  ${methods.mkString("\n")}
               |}""".stripMargin).compilationUnit()

      container
    }
    val semanticType: Type = classes("TestCases")
  }

      //val combi
}
