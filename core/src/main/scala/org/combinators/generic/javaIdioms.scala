package org.combinators.generic

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.cls.types.{Constructor, Type}
import de.tu_dortmund.cs.ls14.twirl.Java

trait JavaIdioms {

  /**
    * Combinator has the power to add fields and methods to the given Compilation Unit.
    * While powerful, it can still cause problems, notably if the methods and/or fields
    * already exist.
    */
  abstract class AugmentCompilationUnit(base: Constructor, conceptType: Symbol) {

    /** Define abstract methods to be overridden by concrete instance. */
    def fields(): Seq[FieldDeclaration]
    def methods(): Seq[MethodDeclaration]

    def apply(unit: CompilationUnit): CompilationUnit = {

      // merge fields into unit's fields
      val types = unit.getTypes
      fields().foreach { x => types.get(0).getMembers.add(x) }
      methods().foreach { x => types.get(0).getMembers.add(x) }

      unit
    }

    /** New type expands by making 'conceptType(base). */
    val semanticType: Type = base =>: conceptType(base)
  }

  /**
    * Create get/set methods for given attribute by name.
    *
    */
  class GetterSetterMethods(att: SimpleName,
    attType: JType,
    base: Constructor,
    conceptType: Symbol) extends AugmentCompilationUnit(base, conceptType) {

    def fields(): Seq[FieldDeclaration] = {
      Java(
        s"""
           |/** Attribute value. */
           |protected $attType $att;
				   """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
    }

    def methods(): Seq[MethodDeclaration] = {
      val capAtt = att.toString().capitalize
      Java(
        s"""
           |/** Get attribute value. */
           |public $attType get$capAtt() {
           |  return this.$att;
           |}
           |
           |/** Set attribute value. */
           |public void set$capAtt($attType $att) {
           |	this.$att = $att;
           |}
					 """.stripMargin).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])
    }
  }


  /**
    * Combine two Seq[Statements], one after the other, and type accordingly
    */
  class StatementCombiner(sem1: Constructor, sem2: Constructor, sem3: Constructor) {
    def apply(head: Seq[Statement], tail: Seq[Statement]): Seq[Statement] = head ++ tail
    val semanticType: Type = sem1 =>: sem2 =>: sem3
  }


  // combinator that deals with IF (GUARD) THEN
  // could also have IF (GUARD) THEN X ELSE Y

  class IfBlock(guard: Constructor, block: Constructor, sem3: Constructor) {
    def apply(guardExpr: Expression, blockStmts: Seq[Statement]): Seq[Statement] = {
      Java(
        s"""
          |if (${guardExpr}) {
          |  ${blockStmts.mkString("\n")}
          |}
				  """.stripMargin).statements()
    }
    val semanticType: Type = guard =>: block =>: sem3
  }
}
