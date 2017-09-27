package org.combinators.solitaire.freecell

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import domain.Solitaire
import org.scalatest._

class FreeCellTests extends FunSpec {

  // free-cell-stuff follows
  lazy val repositoryPre = new game {}
  lazy val GammaPre = ReflectedRepository(repositoryPre, classLoader = this.getClass.getClassLoader)

  lazy val reply: InhabitationResult[Solitaire] = GammaPre.inhabit[Solitaire]('Variation ('FreeCell))
  lazy val iter: Iterator[Solitaire] = reply.interpretedTerms.values.flatMap(_._2).iterator
  lazy val s: Solitaire = iter.next()

  describe("test FreeCell") {
    it("ensure solitaire variation exists and is proper.") {
      assert(s.getTableau.size == 8)
      assert(s.getFoundation.size == 4)
    }
  }
  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  //lazy val fc_repository = new gameDomain(s) // with columnController  with pilecontroller {}

  describe("test FreeCell") {
    describe("when inhabiting solitaire variation") {
      // removes sizes and just gets the second part (i.e., the results)
      lazy val fc_repository = new gameDomain(s)
      lazy val Gamma = ReflectedRepository(fc_repository, classLoader = this.getClass.getClassLoader)
      lazy val job = Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
      lazy val results = job.run().interpretedTerms.values.flatMap(_._2)

      it("ensure just one result") {
        assert(results.size == 1)
      }
      it("check correct class SolitaireVariation exists") {
        assert(results.head.getClassByName("FreeCell").isPresent)
      }
    }

    describe("when inhabiting Column controllers") {
      // removes sizes and just gets the second part (i.e., the results)
      lazy val fc_repository = new gameDomain(s) with columnController with pilecontroller {}
      lazy val Gamma = ReflectedRepository(fc_repository, classLoader = this.getClass.getClassLoader)
      lazy val cc = Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
        .addJob[CompilationUnit]('Controller('Column))

        .run()._2
          .interpretedTerms.values.flatMap(_._2)


      it("ensure just one result") {
        assert(cc.size == 1)
      }
      it("check correct class ColumnController exists") {
        assert(cc.head.getClassByName("ColumnController").isPresent)
      }
    }



    describe("when inhabiting Pile controllers") {
      // removes sizes and just gets the second part (i.e., the results)
      lazy val fc_repository = new gameDomain(s) with columnController with pilecontroller {}
      lazy val Gamma = ReflectedRepository(fc_repository, classLoader = this.getClass.getClassLoader)
      lazy val cc = Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
        .addJob[CompilationUnit]('Controller ('FreePile))
        .run()._2
        .interpretedTerms.values.flatMap(_._2)

      it("ensure just one freePile") {
        assert(cc.size == 1)
      }
      it("check correct class FreePileController exists") {
        assert(cc.head.getClassByName("FreePileController").isPresent)
      }

      lazy val hc = Gamma.InhabitationBatchJob[CompilationUnit]('SolitaireVariation)
        .addJob[CompilationUnit]('Controller ('HomePile))
        .run()._2
        .interpretedTerms.values.flatMap(_._2)

      it("ensure just one homePile") {
        assert(hc.size == 1)
      }
      it("check correct class HomePileController exists") {
        assert(hc.head.getClassByName("HomePileController").isPresent)
      }
    }


      //    .addJob[CompilationUnit]('HomePileClass)
      //    .addJob[CompilationUnit]('FreePileClass)
      //    .addJob[CompilationUnit]('HomePileViewClass)
      //    .addJob[CompilationUnit]('FreePileViewClass)
      //    .addJob[CompilationUnit]('Move('MoveColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildFreePileCard  :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('PlaceColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildColumn :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('PlaceFreePileCard :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('ShuffleFreePile :&: 'GenericMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildFreePileCard :&: 'PotentialMove, 'CompleteMove))
      //    .addJob[CompilationUnit]('Move('BuildColumn :&: 'PotentialMove, 'CompleteMove))


    }
}