package org.combinators.solitaire.idiot

import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait ColumnController extends shared.Controller {

  // column move designated combinators
  @combinator object ColumnControllerDef extends ColumnController('IdiotColumn)

  @combinator object IdiotColumn {
    def apply(): SimpleName = Java("Idiot").simpleName()
    val semanticType: Type = 'Column ('IdiotColumn, 'ClassName)
  }

  //	    val semanticType: Type =
  //      'RootPackage =>:
  //        'Column(columnNameType, 'ClassName) =>:
  //        'NameOfTheGame =>:
  //        'Column(columnNameType, 'Clicked) :&: 'NonEmptySeq =>:
  //        'Column(columnNameType, 'Released) :&: 'NonEmptySeq =>:
  //        'Column(columnNameType, 'Pressed) :&: 'NonEmptySeq =>:
  //        'Controller(columnNameType)


  @combinator object ColumnPressedHandler {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        moves.java.ColumnPressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
    }
    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        'Column ('IdiotColumn, 'Pressed) :&: 'NonEmptySeq
  }

  @combinator object ColumnClickedHandler {
    def apply(pkg: Name, name: SimpleName): Seq[Statement] = {
      moves.java.ColumnClicked.render(pkg, name).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Column ('IdiotColumn, 'Clicked) :&: 'NonEmptySeq
  }

  // release must take into account both FROMPILE and FROMCOLUMN events.
  @combinator object ColumnReleasedHandler {
    def apply(rootPackage: Name): Seq[Statement] = {
      moves.java.ColumnReleased.render(rootPackage).statements()
    }
    val semanticType: Type =
      'RootPackage =>: 'Column ('IdiotColumn, 'Released) :&: 'NonEmptySeq
  }
}