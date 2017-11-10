package example.expression

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import expression.{DomainModel, Exp}
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Collect, Neg, PrettyP, Sub}
import org.webjars.play.WebJarsUtil

import scala.collection.JavaConverters._


class Expression @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  // Configure the desired (sub)types and operations
  // no need to add 'Exp' to the model, since assumed always to be there
  // operations to have (including Eval). Could add SimplifyAdd
  val model:DomainModel = new DomainModel(
    List(new Lit, new Add, new Neg, new Sub).asJava,
    List(new Eval, new PrettyP, new Collect).asJava
  )

  lazy val repository = new ExpressionSynthesis(model) with Structure {}
  import repository._

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), model)

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
      .addJob[CompilationUnit](ops(ops.visitor, new Collect))

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
