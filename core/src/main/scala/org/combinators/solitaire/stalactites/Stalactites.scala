package org.combinators.solitaire.stalactites

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import domain.Solitaire
import org.webjars.play.WebJarsUtil

class Stalactites @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {
  // DOC: ReflectedRepositor? To take all the combinators?
  // DOC: What is classLoader?
  lazy val repositoryPre = new Game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply = GammaPre.inhabit[Solitaire]('Variation('Stalactites))
  lazy val it = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s = it.next()

  lazy val repository = new gameDomain(s) with controllers {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), s)

  lazy val combinatorComponents = Gamma.combinatorComponents

  /*
  * Here I add subclasses, controllers, and moves.
  *   - subclasses are defined in archway/gameDomain.scala,
  *   - controllers are defined in archway/controllers.scala,
  *   - moves are defined in archway/game.scala.
  */
  lazy val jobs =
    Gamma.InhabitationBatchJob[CompilationUnit]('increment('SolitaireVariation))
      .addJob[CompilationUnit]('ReservePileClass)
      .addJob[CompilationUnit]('ReservePileViewClass)
      .addJob[CompilationUnit]('BasePileClass)
      .addJob[CompilationUnit]('BasePileViewClass)

  //      .addJob[CompilationUnit]('increment('SolitaireVariation))
//      .addJob[CompilationUnit]('Controller('StalactitesColumn))
//      .addJob[CompilationUnit]('Controller('ReservePile))
//      .addJob[CompilationUnit]('Controller('Pile))
//      .addJob[CompilationUnit]('lastOrientation('orientation('Move('ColumnToFoundationPile :&: 'PotentialMove, 'CompleteMove))))


//      .add(Gamma.inhabit[CompilationUnit]('lastOrientation('orientation('Move('ColumnToFoundationPile :&: 'GenericMove, 'CompleteMove)))))
//      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToReservePile :&: 'PotentialMove, 'CompleteMove)))
//      .add(Gamma.inhabit[CompilationUnit]('Move('ColumnToReservePile :&: 'GenericMove, 'CompleteMove)))
//      .add(Gamma.inhabit[CompilationUnit]('lastOrientation('orientation('Move('ReservePileToFoundationPile :&: 'PotentialMove, 'CompleteMove)))))
//      .add(Gamma.inhabit[CompilationUnit]('lastOrientation('orientation('Move('ReservePileToFoundationPile :&: 'GenericMove, 'CompleteMove)))))
//      .add(Gamma.inhabit[CompilationUnit]('Move('ReservePileToReservePile :&: 'PotentialMove, 'CompleteMove)))
//      .add(Gamma.inhabit[CompilationUnit]('Move('ReservePileToReservePile :&: 'GenericMove, 'CompleteMove)))

  // Find the Archway Variation.
  lazy val results = Results.addAll(jobs.run())

}