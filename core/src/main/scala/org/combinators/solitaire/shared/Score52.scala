package org.combinators.solitaire.shared

import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java

trait Score52 {
  @combinator object Score52 {
    def apply(): Seq[Statement] = {
      Java("if (getScoreValue() == 52) { return true; }").statements()
    }
    val semanticType: Type = 'WinConditionChecking :&: 'NonEmptySeq
  }
}
