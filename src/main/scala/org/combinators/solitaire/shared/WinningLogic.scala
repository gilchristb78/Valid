package org.combinators.solitaire.shared

import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Java

/**
  * Records the ways in which a game can be marked as won.
  */
trait WinningLogic extends SemanticTypes {

  def createWinLogic[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma

    val logic = s.logic

    logic match {
      case ScoreAchieved(score) =>
        updated = updated.addCombinator (new ScoreBased(score))

      case BoardState(states) =>
        updated = updated.addCombinator (new EvaluateBoardState(s, states))
    }

    updated
  }

  class EvaluateBoardState (sol:Solitaire, states:Map[ContainerType,Int]) {
    def apply(): Seq[Statement] = {
      val init = Java ("boolean hasWon = true;").statements()

      val middle = states.flatMap {
        case (ct, num) =>
          ct match {
            case StockContainer =>
              Java(
                s"""|if (deck.count() != $num) {
                    |   return false;
                    |}""".stripMargin).statements()

            case _ =>
              val name = ct.name

              Java(
                  s"""|{
                      |  int _ct = 0;
                      |  for (Stack st : $name) {
                      |     _ct += st.count();
                      |  }
                      |  if (_ct != $num) {
                      |     return false;
                      |  }
                      |}""".stripMargin).statements()
          }
        }

      init ++ middle ++ Java (s"if (hasWon) { return true; }").statements()
    }

    val semanticType: Type = game(game.winCondition)
  }

  class ScoreBased (score:Int) {
    def apply(): Seq[Statement] = {
      Java(s"""|if (getScoreValue() == $score) {
               |  return true;
               |}""".stripMargin).statements()
    }

    val semanticType: Type = game(game.winCondition)
  }
}
