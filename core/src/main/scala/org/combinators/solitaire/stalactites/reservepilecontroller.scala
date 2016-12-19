package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.body.{BodyDeclaration}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.{Taxonomy, Type}
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait ReservePileController extends shared.Controller  {

	// column move designated combinators
	@combinator object ReservePileControllerDef extends PileController ('ReservePile)

	@combinator object ReservePile {
		def apply(): NameExpr = {
				Java("Stalactites").nameExpression()
		}
		val semanticType: Type = 'Pile('ReservePile, 'ClassName)
	}
	
		// column move designated combinators
	@combinator object FoundationControllerDef extends PileController ('FoundationPile)

	@combinator object FoundationPile {
		def apply(): NameExpr = {
				Java("Foundation").nameExpression()
		}
		val semanticType: Type = 'Pile('FoundationPile, 'ClassName)
	}


}