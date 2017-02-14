package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.body.{BodyDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared
import org.combinators.generic

trait PileToPileMoves extends shared.Moves with generic.JavaIdioms {
  
  
  @combinator object ReservePileToFoundationPileMove extends Move('ReservePileToFoundationPile)
  
  @combinator object ReservePileToFoundationName {
		def apply(): NameExpr = {
			Java("ReservePileToFoundation").nameExpression
	}
	  val semanticType: Type = 'Move('ReservePileToFoundationPile, 'ClassName)
	}
  
  @combinator object PotentialReservePileToFoundationPileMove extends PotentialMove ('ReservePileToFoundationPile)
  
  // the construct needed to hold the card being dragged.
  @combinator object PotentialReservePileToFoundationPileType {
		def apply(): NameExpr = { Java("Card").nameExpression() }
		val semanticType: Type = 'Move('ReservePileToFoundationPile, 'TypeConstruct)
	}
	
  @combinator object PotentialReservePileToFoundationDraggingVariable {
		def apply(): NameExpr = {
				Java("movingCard").nameExpression()
		}
		val semanticType: Type = 'Move('ReservePileToFoundationPile, 'DraggingCardVariableName)
	}

  @combinator object ReservePileToFoundationPileValid {
		def apply(root:NameExpr, name:NameExpr): Seq[Statement] = {
				moves.reservepiletofoundationpile.java.ReservePileToFoundationPileValid.render(root, name).statements()
		}
		val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Move('ReservePileToFoundationPile, 'CheckValidStatements)
	}
  
	@combinator object ReservePileToFoundationHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.reservepiletofoundationpile.java.ReservePileToFoundationPileMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('ReservePileToFoundationPile, 'ClassName) =>: 'Move('ReservePileToFoundationPile, 'HelperMethods)
	}
	
	@combinator object ReservePileToFoundationMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.add(movingCard);").statements()
		}
		val semanticType: Type = 'MoveCardRPFP
	}

	@combinator object ReservePileToFoundationMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""
						source.add(destination.get());
						""").statements()
		}
		val semanticType: Type = 'UndoCardRPFP
	}
	
		/** Compose these behaviors. */
	@combinator object DoRPFP extends StatementCombiner('MoveCardRPFP, 'IncrementScore, 'Move('ReservePileToFoundationPile, 'DoStatements))
	@combinator object UndoRPFP extends StatementCombiner('UndoCardRPFP, 'DecrementScore, 'Move('ReservePileToFoundationPile, 'UndoStatements))
	
	
	// and injecting them into the compilation unit.
	@combinator object OrientationRPFPOCombinator extends GetterSetterMethods(Java("orientation").nameExpression(), "int", 'Move('ReservePileToFoundationPile :&: 'GenericMove, 'CompleteMove), 'orientation)
  @combinator object OrientationPRPFPOCombinator extends GetterSetterMethods(Java("orientation").nameExpression(), "int", 'Move('ReservePileToFoundationPile :&: 'PotentialMove, 'CompleteMove), 'orientation)
  
  @combinator object OrientationRPFPCombinator extends GetterSetterMethods(Java("lastOrientation").nameExpression(), "int", 'orientation('Move('ReservePileToFoundationPile :&: 'GenericMove, 'CompleteMove)), 'lastOrientation)
  @combinator object OrientationPRPFPCombinator extends GetterSetterMethods(Java("lastOrientation").nameExpression(), "int", 'orientation('Move('ReservePileToFoundationPile :&: 'PotentialMove, 'CompleteMove)), 'lastOrientation)
  
	
	
	/// ----------------------- THESE ARE TO THE RESERVE ------------------
	
	@combinator object ReservePileToReservePileMove extends Move('ReservePileToReservePile)
  
	// THIS WAS MISSING apply() but rather had apply without () and no error/warning.
  @combinator object ReservePileToReserveName {
		def apply(): NameExpr = {
			Java("ReservePileToReserve").nameExpression
	}
	  val semanticType: Type = 'Move('ReservePileToReservePile, 'ClassName)
	}
  
  @combinator object PotentialReservePileToReservePileMove extends PotentialMove ('ReservePileToReservePile)
  
  // the construct needed to hold the card being dragged.
  @combinator object PotentialReservePileToReservePileType {
		def apply(): NameExpr = { Java("Card").nameExpression() }
		val semanticType: Type = 'Move('ReservePileToReservePile, 'TypeConstruct)
	}
	
  @combinator object PotentialReservePileToReserveDraggingVariable {
		def apply(): NameExpr = {
				Java("movingCard").nameExpression()
		}
		val semanticType: Type = 'Move('ReservePileToReservePile, 'DraggingCardVariableName)
	}

  @combinator object ReservePileToReservePileValid {
		def apply(): Seq[Statement] = {
				moves.reservepiletoreservepile.java.ReservePileToReservePileValid.render().statements()
		}
		val semanticType: Type = 'Move('ReservePileToReservePile, 'CheckValidStatements)
	}
  
	@combinator object ReservePileToReserveHelper {
		def apply(name:NameExpr): Seq[BodyDeclaration] = {
				moves.reservepiletoreservepile.java.ReservePileToReservePileMoveHelper.render(name).classBodyDeclarations()
		}
		val semanticType: Type = 'Move('ReservePileToReservePile, 'ClassName) =>: 'Move('ReservePileToReservePile, 'HelperMethods)
	}
	
	@combinator object ReservePileToReserveMoveDo {
		def apply(): Seq[Statement] = {
				Java("destination.add(movingCard);").statements()
		}
		val semanticType: Type = 'Move('ReservePileToReservePile, 'DoStatements)
	}

	@combinator object ReservePileToReserveMoveUndo {
		def apply(): Seq[Statement] = {
				Java("""
						source.add(destination.get());
						""").statements()
		}
		val semanticType: Type = 'Move('ReservePileToReservePile, 'UndoStatements)
	}
	

}