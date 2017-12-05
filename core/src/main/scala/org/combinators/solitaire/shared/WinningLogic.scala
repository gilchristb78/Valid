package org.combinators.solitaire.shared

import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.{Solitaire, SolitaireContainerTypes}
import domain.win.{BoardState, BoardStatePair, ScoreAchieved}

import scala.collection.JavaConverters._

/**
  * Records the ways in which a game can be marked as won.
  */
trait WinningLogic extends SemanticTypes {

  def createWinLogic[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    val logic = s.getLogic

    logic match {
      case sa:ScoreAchieved =>
        updated = updated.addCombinator (new ScoreBased(sa))

      case bs: BoardState =>
        updated = updated.addCombinator (new EvaluateBoardState(s, bs))
    }

    updated
  }

  class EvaluateBoardState (sol:Solitaire, bs:BoardState) {
    def apply(): Seq[Statement] = {
      var stmts:Seq[Statement] = Java ("boolean hasWon = true;").statements()

      for (pair:BoardStatePair <- bs.elements.asScala) {
        val name = pair.tpe.toString

        pair.tpe match {
          case SolitaireContainerTypes.Stock => stmts = stmts ++
            Java(
              s"""|if (deck.count() != ${pair.total}) {
                  |   return false;
                  |}""".stripMargin).statements()

          case _ =>
            val name = typeOfContainer(pair.tpe)

            stmts = stmts ++
            Java(s"""|{
                |  int _ct = 0;
                |  for (Stack st : $name) {
                |     _ct += st.count();
                |  }
                |  if (_ct != ${pair.total}) {
                |     return false;
                |  }
                |}""".stripMargin).statements()
        }
      }

      stmts ++ Java (s"if (hasWon) { return true; }").statements()
    }

    val semanticType: Type = game(game.winCondition)
  }

  class ScoreBased (sc:ScoreAchieved) {
    def apply(): Seq[Statement] = {
      Java(s"""|if (getScoreValue() == ${sc.score}) {
               |  return true;
               |}""".stripMargin).statements()
    }

    val semanticType: Type = game(game.winCondition)
  }
}
