package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import org.combinators.solitaire.shared

trait PileToPileMoves extends shared.Moves with generic.JavaIdioms {
//  @combinator object ReservePileToFoundationPileMove extends SolitaireMove('ReservePileToFoundationPile)
//
//  @combinator object ReservePileToFoundationName {
//    def apply(): SimpleName = Java("ReservePileToFoundation").simpleName()
//    val semanticType: Type = 'Move ('ReservePileToFoundationPile, 'ClassName)
//  }
//
//  @combinator object PotentialReservePileToFoundationPileMove extends PotentialMoveSingleCard('ReservePileToFoundationPile)
//
//  // the construct needed to hold the card being dragged.
//  @combinator object PotentialReservePileToFoundationPileType {
//    def apply(): JType = Java("Card").tpe()
//    val semanticType: Type = 'Move ('ReservePileToFoundationPile, 'SingleCardMove)
//  }
//
//  @combinator object PotentialReservePileToFoundationDraggingVariable {
//    def apply(): SimpleName = Java("movingCard").simpleName()
//    val semanticType: Type = 'Move ('ReservePileToFoundationPile, 'DraggingCardVariableName)
//  }
//
//  @combinator object ReservePileToFoundationPileValid {
//    def apply(root: Name, name: SimpleName): Seq[Statement] = {
//      moves.reservepiletofoundationpile.java.ReservePileToFoundationPileValid.render(root, name).statements()
//    }
//    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Move ('ReservePileToFoundationPile, 'CheckValidStatements)
//  }
//
//  @combinator object ReservePileToFoundationHelper {
//    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
//      moves.reservepiletofoundationpile.java.ReservePileToFoundationPileMoveHelper.render(name).classBodyDeclarations()
//    }
//    val semanticType: Type =
//      'Move ('ReservePileToFoundationPile, 'ClassName) =>: 'Move ('ReservePileToFoundationPile, 'HelperMethods)
//  }
//
//  @combinator object ReservePileToFoundationMoveDo {
//    def apply(): Seq[Statement] = Java("destination.add(movingCard);").statements()
//    val semanticType: Type = 'MoveCardRPFP
//  }
//
//  @combinator object ReservePileToFoundationMoveUndo {
//    def apply(): Seq[Statement] = Java("source.add(destination.get());").statements()
//    val semanticType: Type = 'UndoCardRPFP
//  }
//
//  /** Compose these behaviors. */
//  @combinator object DoRPFP
//    extends StatementCombiner('MoveCardRPFP, 'IncrementScore, 'Move ('ReservePileToFoundationPile, 'DoStatements))
//  @combinator object UndoRPFP
//    extends StatementCombiner('UndoCardRPFP, 'DecrementScore, 'Move ('ReservePileToFoundationPile, 'UndoStatements))
//
//
//  // and injecting them into the compilation unit.
//  @combinator object OrientationRPFPOCombinator
//    extends GetterSetterMethods(
//      Java("orientation").simpleName(),
//      Java("int").tpe(),
//      'Move ('ReservePileToFoundationPile :&: 'GenericMove, 'CompleteMove),
//      'orientation)
//  @combinator object OrientationPRPFPOCombinator
//    extends GetterSetterMethods(
//      Java("orientation").simpleName(),
//      Java("int").tpe(),
//      'Move ('ReservePileToFoundationPile :&: 'PotentialMove, 'CompleteMove),
//      'orientation)
//
//  @combinator object OrientationRPFPCombinator
//    extends GetterSetterMethods(
//      Java("lastOrientation").simpleName(),
//      Java("int").tpe(),
//      'orientation ('Move ('ReservePileToFoundationPile :&: 'GenericMove, 'CompleteMove)),
//      'lastOrientation)
//  @combinator object OrientationPRPFPCombinator
//    extends GetterSetterMethods(
//      Java("lastOrientation").simpleName(),
//      Java("int").tpe(),
//      'orientation ('Move ('ReservePileToFoundationPile :&: 'PotentialMove, 'CompleteMove)),
//      'lastOrientation)
//
//
//  /// ----------------------- THESE ARE TO THE RESERVE ------------------
//
//  @combinator object ReservePileToReservePileMove extends SolitaireMove('ReservePileToReservePile)
//
//  // THIS WAS MISSING apply() but rather had apply without () and no error/warning.
//  @combinator object ReservePileToReserveName {
//    def apply(): SimpleName = Java("ReservePileToReserve").simpleName()
//    val semanticType: Type = 'Move ('ReservePileToReservePile, 'ClassName)
//  }
//
//  @combinator object PotentialReservePileToReservePileMove extends PotentialMoveSingleCard('ReservePileToReservePile)
//
//  // the construct needed to hold the card being dragged.
//  @combinator object PotentialReservePileToReservePileType {
//    def apply(): JType = Java("Card").tpe()
//    val semanticType: Type = 'Move ('ReservePileToReservePile, 'SingleCardMove)
//  }
//
//  @combinator object PotentialReservePileToReserveDraggingVariable {
//    def apply(): SimpleName = Java("movingCard").simpleName()
//    val semanticType: Type = 'Move ('ReservePileToReservePile, 'DraggingCardVariableName)
//  }
//
//  @combinator object ReservePileToReservePileValid {
//    def apply(): Seq[Statement] = {
//      moves.reservepiletoreservepile.java.ReservePileToReservePileValid.render().statements()
//    }
//    val semanticType: Type = 'Move ('ReservePileToReservePile, 'CheckValidStatements)
//  }
//
//  @combinator object ReservePileToReserveHelper {
//    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
//      moves.reservepiletoreservepile.java.ReservePileToReservePileMoveHelper.render(name).classBodyDeclarations()
//    }
//    val semanticType: Type =
//      'Move ('ReservePileToReservePile, 'ClassName) =>: 'Move ('ReservePileToReservePile, 'HelperMethods)
//  }
//
//  @combinator object ReservePileToReserveMoveDo {
//    def apply(): Seq[Statement] = Java("destination.add(movingCard);").statements()
//    val semanticType: Type = 'Move ('ReservePileToReservePile, 'DoStatements)
//  }
//
//  @combinator object ReservePileToReserveMoveUndo {
//    def apply(): Seq[Statement] = Java("source.add(destination.get());").statements()
//    val semanticType: Type = 'Move ('ReservePileToReservePile, 'UndoStatements)
//  }
}
