package org.combinators.generic

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.{BodyDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{NameExpr, Expression}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.{Taxonomy, Type, Constructor}
import de.tu_dortmund.cs.ls14.cls.types.syntax._

import de.tu_dortmund.cs.ls14.twirl.Java

trait JavaIdioms {
	
	/**
	 * Combine two Seq[Statements], one after the other, and type accordingly
	 */
	class StatementCombiner(sem1:Constructor, sem2:Constructor, sem3:Constructor) {
	  def apply(head:Seq[Statement], tail:Seq[Statement]): Seq[Statement] = { 
	    Java(head.mkString("\n") + "\n" + tail.mkString("\n")).statements()
	  }
	  
	  val semanticType: Type = sem1 =>: sem2 =>: sem3
	}
	
	
	/**
	 * Create get/set methods for given attribute by name.
	 * 
	 * Hack: not yet able to use (com.github.javaparser.ast.`type`.Type) since not part of Java() base class.
	 */
	class GetterSetterMethods(att:NameExpr, attType:String, symbol:Symbol) {
	  def apply(): Seq[MethodDeclaration] = { 
	    val capAtt = att.toString().capitalize
	    Java(s"""
          /** Get attribute value. */
          public $attType get$capAtt() {
            return this.$att;
          }
          
          /** Set attribute value. */
          public void set$capAtt($attType $att) {
            this.$att = $att;
          }""").classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])
	  }
	  
	  val semanticType: Type = symbol
	}
	
	
	// combinator that deals with IF (GUARD) THEN
	// could also have IF (GUARD) THEN X ELSE Y
	
	class IfBlock(guard:Constructor, block:Constructor, sem3:Constructor) {
	  def apply(guardExpr:Expression, blockStmts:Seq[Statement]): Seq[Statement] = { 
	    Java("if (" + guardExpr.toString() + ")\n { " + blockStmts.mkString("\n") + "}").statements()
	  }
	  
	  val semanticType: Type = guard =>: block =>: sem3
	}
}