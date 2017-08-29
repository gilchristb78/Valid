package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.generic
import org.combinators.solitaire.shared.{GameTemplate, Score52}

trait Game extends GameTemplate with Score52 with generic.JavaIdioms {

  @combinator object NumReservePiles {
    def apply: Expression = Java("2").expression()
    val semanticType: Type = 'NumReservePiles
  }

  @combinator object NumFoundations {
    def apply: Expression = Java("4").expression()
    val semanticType: Type = 'NumFoundations
  }

  @combinator object NumColumns {
    def apply: Expression = Java("8").expression()
    val semanticType: Type = 'NumColumns
  }

  @combinator object RootPackage {
    def apply: Name = Java("org.combinators.solitaire.stalactites").name()
    val semanticType: Type = 'RootPackage
  }

  @combinator object NameOfTheGame {
    def apply: SimpleName = Java("Stalactites").simpleName()
    val semanticType: Type = 'NameOfTheGame
  }

  // NumColumns: Expression, NumReservePiles: Expression, NumFoundations: Expression)
  @combinator object InitializationCode {
    def apply(numColumns: Expression, numReservePiles: Expression, numFoundations: Expression): Seq[Statement] = {
      java.Initialization.render(numColumns, numReservePiles, numFoundations).statements()
    }
    val semanticType: Type = 'NumColumns =>: 'NumReservePiles =>: 'NumFoundations =>: 'Initialization :&: 'NonEmptySeq
  }

  @combinator object ExtraImports {
    def apply(rootPkg: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $rootPkg.controller.*;").importDeclaration(),
        Java(s"import $rootPkg.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = 'RootPackage =>: 'ExtraImports
  }

  // desire to consolidate into one place everything having to do with increment capability
  // Defined in one place and then. Cross-cutting combinator.


  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty
    val semanticType: Type = 'ExtraMethods
  }

  // takes in concept to be added.
  abstract class WeaveCombinator(conceptType: Symbol) {

    def fields(): Seq[FieldDeclaration]
    def methods(): Seq[MethodDeclaration]

    def apply(unit: CompilationUnit): CompilationUnit = {

      // merge fields into unit's fields
      val types = unit.getTypes
      fields().foreach { x => types.get(0).getMembers.add(x) }
      methods().foreach { x => types.get(0).getMembers.add(x) }

      unit
    }

    val semanticType: Type = 'SolitaireVariation =>: conceptType('SolitaireVariation)
  }

  // Finally applies the weaving of the Increment concept by takings its constituent fields and method declarations
  // and injecting them into the compilation unit.
  @combinator object IncrementCombinator
    extends GetterSetterMethods(
      Java("increment").simpleName(),
      Java("int").tpe(),
      'SolitaireVariation,
      'increment)


  /**
    * Fundamental to Undo of moves to foundation is the ability to keep track of the last orientation.
    */
  @combinator object RecordNewOrientation {
    def apply(root: Name, name: SimpleName): Seq[Statement] = {
      Java(
        s"""
           |$root.$name stalactites = ($root.$name)game;
           |lastOrientation = stalactites.getIncrement();
           |stalactites.setIncrement(orientation);
           """.stripMargin).statements()
    }

    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'RecordOrientation
  }

  /**
    * Go back to earlier orientation
    */
  @combinator object RevertToEarlierOrientation {
    def apply(root: Name, name: SimpleName): Seq[Statement] = {
      Java(
        s"""
           |$root.$name stalactites = ($root.$name)game;
           |stalactites.setIncrement(lastOrientation);
           """.stripMargin).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'UndoOrientation
  }


  // @(NumColumns: Expression, NumReservePiles: Expression, NumFoundations: Expression)
  @combinator object ExtraFields {
    def apply(numColumns: Expression,
      numReservePiles: Expression,
      numFoundations: Expression): Seq[FieldDeclaration] = {
      java.ExtraFields
        .render(numColumns, numReservePiles, numFoundations)
        .classBodyDeclarations()
        .map(_.asInstanceOf[FieldDeclaration])
    }
    val semanticType: Type = 'NumColumns =>: 'NumReservePiles =>: 'NumFoundations =>: 'ExtraFields
  }
}
