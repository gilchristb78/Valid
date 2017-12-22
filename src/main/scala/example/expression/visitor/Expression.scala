package example.expression.visitor

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Collect, Neg, PrettyP, Sub}
import expression.operations.SimplifyAdd
import expression.{DomainModel, Exp, Operation}
import org.webjars.play.WebJarsUtil

import scala.collection.JavaConverters._

class Expression @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  // Configure the desired (sub)types and operations
  // no need to add 'Exp' to the model, since assumed always to be there
  // operations to have (including Eval).
  val first:DomainModel = new DomainModel(
    List[Exp](new Lit, new Add, new Sub).asJava,
    List[Operation](new Eval, new PrettyP).asJava
  )

  // Extension to domain model has new data variants and operations
  val other:DomainModel = new DomainModel(
    List[Exp](new Neg).asJava,
    List[Operation](new Collect, new SimplifyAdd).asJava
  )

  // demonstrate how to merge domain models with new capabilities
  // supported by POJO domain model
  val model = first.merge(other)

  lazy val repository = new ExpressionSynthesis(model) with Structure {}
  import repository._

  var Gamma = ReflectedRepository(repository, classLoader = this.getClass.getClassLoader)

  // Programmatically add combinators based on domain model.
  domain.data.asScala.foreach {
    sub:Exp => {
      Gamma = Gamma
        .addCombinator (new BaseClass(sub))
        .addCombinator (new ImplClass(sub))
    }
  }

  domain.ops.asScala.foreach {
    op:Operation => {
      Gamma = Gamma
        .addCombinator (new OpImpl(op))
    }
  }

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinatorComponents = Gamma.combinatorComponents

  // Quickest way to request all targets
  var jobs = Gamma.InhabitationBatchJob[CompilationUnit](generated(generated.visitor))
      .addJob[CompilationUnit](exp(exp.base, new Exp))
      .addJob[CompilationUnit](ops(ops.visitor, new Eval))
      .addJob[CompilationUnit](exp(exp.visitor, new Lit))
      .addJob[CompilationUnit](exp(exp.visitor, new Add))
      .addJob[CompilationUnit](exp(exp.visitor, new Sub))
      .addJob[CompilationUnit](exp(exp.visitor, new Neg))
      .addJob[CompilationUnit](ops(ops.visitor, new PrettyP))
      .addJob[CompilationUnit](ops(ops.visitor, new Collect))
      .addJob[CompilationUnit](ops(ops.visitor, new SimplifyAdd))

  lazy val results = Results.addAll(jobs.run())
}
