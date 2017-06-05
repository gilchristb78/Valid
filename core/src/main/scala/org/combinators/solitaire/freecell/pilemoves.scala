package org.combinators.solitaire.freecell

import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import domain.constraints._
import domain._
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import com.github.javaparser.ast.CompilationUnit


trait PileMoves extends shared.Moves {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println (">>>> PileMoves dynamic combinators")
      
      val truth = new ReturnConstraint (new ReturnTrueExpression)
      val falsehood = new ReturnConstraint (new ReturnFalseExpression)
      val isEmpty = new ElementEmpty ("destination")

         
      // FreePile to FreePile logic
      val if0 = new IfConstraint(isEmpty)
      updated = updated
         .addCombinator (new StatementCombinator (if0,
			 'Move ('FreePileToFreePile, 'CheckValidStatements)))

 
      // Column To Free Pile Logic
      val isSingle = new ExpressionConstraint("movingColumn.count()", "==", "1")
      val if1 = new IfConstraint(isEmpty, 
		  new IfConstraint(isSingle),
                falsehood)

      updated = updated
          .addCombinator (new StatementCombinator (if1, 
	   	          'Move ('ColumnToFreePile, 'CheckValidStatements)))

      // Column To Home Pile logic. Just grab first column
      val aCol = s.getTableau().iterator().next()

      val if2 = 
        new IfConstraint(isEmpty, 
           new IfConstraint(new IsAce(aCol, "movingColumn")),
           new IfConstraint(new NextRank("movingColumn.peek()", "destination.peek()"),
	       new IfConstraint(new SameSuit("movingColumn.peek()", "destination.peek()")),
	       falsehood))

      updated = updated
          .addCombinator (new StatementCombinator(if2, 
			 'Move ('ColumnToHomePile, 'CheckValidStatements)))

      // FreePile to HomePile 
      val aCard = new Card
      val nonEmpty = new ExpressionConstraint("destination.count()", "!=", "0")

      val if3 = 
         new IfConstraint(isEmpty,
	    new IfConstraint(new IsAce(aCard, "movingCard")),
            new IfConstraint(new NextRank("movingCard", "destination.peek()"),
	      new IfConstraint(new SameSuit("movingCard", "destination.peek()")),
            falsehood))

      updated = updated
          .addCombinator (new StatementCombinator(if3,
                         'Move ('FreePileToHomePile, 'CheckValidStatements)))

      // FreePile to Column.
     val if5_inner =
          new IfConstraint(new OppositeColor("movingCard", "destination.peek()"),
            new IfConstraint(new NextRank("destination.peek()", "movingCard")),
            falsehood)

      val if5 = new IfConstraint(isEmpty, truth, if5_inner)
 
      updated = updated
          .addCombinator (new StatementCombinator (if5, 
			  'Move ('FreePileToColumn, 'CheckValidStatements)))

      updated
  }


  @combinator object PotentialPileToColumnMoveObject extends PotentialMove('FreePileToColumn)
  @combinator object PotentialFreePileToHomePileMoveObject extends PotentialMove('FreePileToHomePile)
  @combinator object PotentialFreePileToFreePileMoveObject extends PotentialMove('FreePileToFreePile)

 @combinator object PotentialColumnToFreePileMoveObject extends PotentialMoveOneCardFromStack('ColumnToFreePile)
  @combinator object PotentialColumnToHomePileMoveObject extends PotentialMoveOneCardFromStack('ColumnToHomePile)

  @combinator object PotentialStackMoveFree {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move ('ColumnToFreePile, 'TypeConstruct)
  }
  @combinator object PotentialStackMoveHome {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move ('ColumnToHomePile, 'TypeConstruct)
  }
}
