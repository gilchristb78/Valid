package example.expression

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.java.JavaPersistable._
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Collect, Neg, PrettyP, Sub}
import expression.operations.SimplifyAdd
import expression.{DomainModel, Exp}
import org.webjars.play.WebJarsUtil

// https://bitbucket.org/yanlinwang/ep_trivially/src/7086d91a45c92c1522ec4d6f0618c574c2e2d562/JavaCode/EP/src/interfaceversion/InterfaceVersion.java?at=master&fileviewer=file-view-default

class Expression_EP @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  // Configure the desired (sub)types and operations
  val model:DomainModel = new DomainModel()

  // no need to add 'Exp' to the model, since assumed always to be there
  model.data.add(new Lit)
  model.data.add(new Add)
  model.data.add(new Neg)
  model.data.add(new Sub)

  // operations to have (including Eval)
  model.ops.add(new Eval)
  model.ops.add(new PrettyP)
  //model.ops.add(new SimplifyAdd)
  model.ops.add(new Collect)

  lazy val repository = new EP_ExpressionSynthesis(model) with EP {}
  import repository._

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), model)

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinatorComponents = Gamma.combinatorComponents

  var jobs = Gamma.InhabitationBatchJob[CompilationUnit](ep(ep.interface, new Exp))
    // type interfaces (note: Exp is assumed above)
    .addJob[CompilationUnit](ep(ep.interface, new PrettyP))
    .addJob[CompilationUnit](ep(ep.interface, new Collect))

    .addJob[CompilationUnit](driver)
    .addJob[CompilationUnit](ep(ep.finalType, new Lit))
    .addJob[CompilationUnit](ep(ep.finalType, new Add))
    .addJob[CompilationUnit](ep(ep.finalType, new Sub))
    .addJob[CompilationUnit](ep(ep.finalType, new Neg))

    // default
    .addJob[CompilationUnit](ep(ep.defaultMethods, new Lit, new Eval))
    .addJob[CompilationUnit](ep(ep.defaultMethods, new Add, new Eval))
    .addJob[CompilationUnit](ep(ep.defaultMethods, new Sub, new Eval))
    .addJob[CompilationUnit](ep(ep.defaultMethods, new Neg, new Eval))

    //.addJob[CompilationUnit](ep(ep.interface, new SimplifyAdd))
    .addJob[CompilationUnit](ep(ep.interface, new Lit, new PrettyP))
    .addJob[CompilationUnit](ep(ep.interface, new Add, new PrettyP))
    .addJob[CompilationUnit](ep(ep.interface, new Sub, new PrettyP))
    .addJob[CompilationUnit](ep(ep.interface, new Neg, new PrettyP))

    .addJob[CompilationUnit](ep(ep.interface, new Lit, new Collect))
    .addJob[CompilationUnit](ep(ep.interface, new Add, new Collect))
    .addJob[CompilationUnit](ep(ep.interface, new Sub, new Collect))
    .addJob[CompilationUnit](ep(ep.interface, new Neg, new Collect))

    .addJob[CompilationUnit](ep(ep.interface, new Lit, List(new PrettyP, new Collect)))
    .addJob[CompilationUnit](ep(ep.interface, new Add, List(new PrettyP, new Collect)))
    .addJob[CompilationUnit](ep(ep.interface, new Sub, List(new PrettyP, new Collect)))
    .addJob[CompilationUnit](ep(ep.interface, new Neg, List(new PrettyP, new Collect)))

    .addJob[CompilationUnit](ep(ep.interface, List(new PrettyP, new Collect)))

    .addJob[CompilationUnit](ep(ep.finalType, new Lit, List(new PrettyP)))
    .addJob[CompilationUnit](ep(ep.finalType, new Add, List(new PrettyP)))
    .addJob[CompilationUnit](ep(ep.finalType, new Sub, List(new PrettyP)))
    .addJob[CompilationUnit](ep(ep.finalType, new Neg, List(new PrettyP)))


    .addJob[CompilationUnit](ep(ep.finalType, new Lit, List(new Collect)))
    .addJob[CompilationUnit](ep(ep.finalType, new Add, List(new Collect)))
    .addJob[CompilationUnit](ep(ep.finalType, new Sub, List(new Collect)))
    .addJob[CompilationUnit](ep(ep.finalType, new Neg, List(new Collect)))

    .addJob[CompilationUnit](ep(ep.finalType, new Lit, List(new PrettyP, new Collect)))
    .addJob[CompilationUnit](ep(ep.finalType, new Add, List(new PrettyP, new Collect)))
    .addJob[CompilationUnit](ep(ep.finalType, new Sub, List(new PrettyP, new Collect)))
    .addJob[CompilationUnit](ep(ep.finalType, new Neg, List(new PrettyP, new Collect)))



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
