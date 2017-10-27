package org.combinators.solitaire.shared

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.{InhabitationResult, ReflectedRepository}
import de.tu_dortmund.cs.ls14.cls.types.Type
import domain.Solitaire
import org.scalatest._


/**
  * Defines helpers
  */
class Helper (types:SemanticTypes) extends FunSpec {

  // Can't seem to place 'it' methods within this function
  def singleClass(name: String, result: InhabitationResult[CompilationUnit]):Boolean = {
    val inhab: Iterator[CompilationUnit] = result.interpretedTerms.values.flatMap(_._2).iterator

   if (inhab.hasNext) {
     val actual = inhab.next
     val clazz = actual.getClassByName(name)
     if (!inhab.hasNext) {
       return clazz.isPresent && clazz.get().getNameAsString == name
     }
   }

   false
  }

  /**
    * Determine if a single instance of given type.
    *
    * @param result
    * @tparam T
    * @return
    */
  def singleInstance[T](result: InhabitationResult[T]):Boolean = {
    val inhab: Iterator[T] = result.interpretedTerms.values.flatMap(_._2).iterator

    if (inhab.hasNext) {
      val actual = inhab.next   // advance
      if (!inhab.hasNext) {
        return true
      }
    }

    false
  }


}
