package example.expression

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import expression.{DomainModel, Exp}
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Neg, PrettyP, Sub}
import expression.operations.SimplifyAdd
import org.webjars.play.WebJarsUtil


class Expression @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  // Configure the desired (sub)types and operations
  val model:DomainModel = new DomainModel()

  // no need to add 'Exp' to the model, since assumed always to be there
  model.data.add(new Lit)
  model.data.add(new Add)
  model.data.add(new Neg)
  model.data.add(new Sub)

  // operations (other than Eval) which is assumed to always be there.
  model.ops.add(new PrettyP)
  lazy val repository = new ExpressionSynthesis(model)
  import repository._

  // not needed yet since we do not have dynamic combinators
  // kinding is just a field access.
  lazy val Gamma = ReflectedRepository(repository, classLoader = this.getClass.getClassLoader)

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinatorComponents = Gamma.combinatorComponents

  var jobs = Gamma.InhabitationBatchJob[CompilationUnit](generated(generated.visitor))
      .addJob[CompilationUnit](exp(exp.base, new Exp))
      .addJob[CompilationUnit](ops(ops.visitor, new Eval))    // assumed to always be there...

      .addJob[CompilationUnit](exp(exp.visitor, new Lit))
      .addJob[CompilationUnit](exp(exp.visitor, new Add))
      .addJob[CompilationUnit](exp(exp.visitor, new Sub))
      .addJob[CompilationUnit](exp(exp.visitor, new Neg))
      .addJob[CompilationUnit](ops(ops.visitor, new PrettyP))
      .addJob[CompilationUnit](ops(ops.visitor, new SimplifyAdd))
      .addJob[CompilationUnit](driver)

//
//  // wish I could do something like this, but I need help...
//  model.data.asScala.foreach {
//    sub:Exp => jobs.addJob[CompilationUnit](exp(exp.visitor, sub))
//  }
//
//  model.ops.asScala.foreach {
//    op:Operation => jobs.addJob[CompilationUnit](ops(ops.visitor, op))
//  }

  lazy val results = Results.addAll(jobs.run())

}
