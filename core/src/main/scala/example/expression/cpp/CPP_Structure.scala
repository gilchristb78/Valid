package example.expression.cpp

import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import example.expression.{Base, ExpressionDomain}
import expression._
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Collect, Neg, PrettyP, Sub}
import expression.types.Types
import expression.operations.SimplifyAdd

import scala.collection.JavaConverters._

/**
  * C++ Solution using just String manipulation
  *
  * https://github.com/eliben/code-for-blog/blob/master/2016/expression-problem/c%2B%2B/visitor-dispatch-in-data.cpp
  */
trait CPP_Structure extends Base with CPPSemanticTypes {

  /** Add dynamic combinators as needed. */
  override def init[G <: ExpressionDomain](gamma: ReflectedRepository[G], model: DomainModel): ReflectedRepository[G] = {
    var updated = super.init(gamma, model)

    def registerImpl (op:Operation, map:Map[Exp,String]): Unit = {
      map.keys.foreach {
        key =>
          addImpl(op, key, map(key))
      }
    }

    // implementations of operations: have to be defined before combinators?
    // consider codegeneratorregistry as before with constraints
    registerImpl(new Eval, Map(
      new Lit -> "value_map_[e] = *e->getValue();",
      new Add -> "value_map_[e] = value_map_[e->getLeft()] + value_map_[e->getRight()];",
      new Sub -> "value_map_[e] = value_map_[e->getLeft()] - value_map_[e->getRight()];",
      new Neg -> "value_map_[e] = -value_map_[e->getExp()];"
    ))

    registerImpl(new PrettyP, Map(
      new Lit -> """|std::ostringstream ss;
                    |    ss << *e->getValue();
                    |    value_map_[e] = ss.str();""".stripMargin,
      new Add -> """value_map_[e] = "(" + value_map_[e->getLeft()] + "+" + value_map_[e->getRight()] + ")";""",
      new Sub -> """value_map_[e] = "(" + value_map_[e->getLeft()] + "-" + value_map_[e->getRight()] + ")";""",
      new Neg -> """value_map_[e] = "-" + value_map_[e->getExp()];"""
    ))

    val combined:String =
      """
        |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
        |list.addAll(e.getLeft().accept(this));
        |list.addAll(e.getRight().accept(this));
        |return list;
      """.stripMargin

    registerImpl(new Collect, Map(
      new Lit -> """
                  |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
                  |list.add(e.getValue());
                  |return list;
                  """.stripMargin,
      new Add -> combined,
      new Sub -> combined,
      new Neg -> """
                   |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
                   |list.addAll(e.getExp().accept(this));
                   |return list;
                 """.stripMargin
    ))

    addImpl(new SimplifyAdd, new Lit, s"""return e;""")   // nothing to simplify.
    addImpl(new SimplifyAdd, new Neg,
      s"""
         |if (e.getExp().accept(new Eval()) == 0) {
         |  return new Lit(0);
         |} else {
         |  return e;
         |}
         """.stripMargin)

    addImpl(new SimplifyAdd, new Sub,
      s"""
         |if (e.getLeft().accept(new Eval()) == e.getRight().accept(new Eval())) {
         |  return new Lit(0);
         |} else {
         |  return new Sub(e.getLeft().accept(this), e.getRight().accept(this));
         |}
         |""".stripMargin)

    addImpl(new SimplifyAdd, new Add,
      s"""
         |int leftVal = e.getLeft().accept(new Eval());
         |int rightVal = e.getRight().accept(new Eval());
         |if ((leftVal == 0 && rightVal == 0) || (leftVal + rightVal == 0)) {
         |  return new Lit(0);
         |} else if (leftVal == 0) {
         |  return e.getRight().accept(this);
         |} else if (rightVal == 0) {
         |  return e.getLeft().accept(this);
         |} else {
         |  return new Add(e.getLeft().accept(this), e.getRight().accept(this));
         |}
         |""".stripMargin)


    // Add relevant combinators to construct the sub-type classes, based on domain model.
      model.data.asScala.foreach {
        sub:Exp => {
          updated = updated
            .addCombinator (new BaseClass(sub))
            .addCombinator (new ImplClass(sub))
        }
      }

    model.ops.asScala.foreach {
      op:Operation => {
        updated = updated
          .addCombinator (new OpImpl(op))
      }
    }

    // create packaging
    updated = updated
      .addCombinator(new BaseModule(model))

    updated
  }

  /** Works on any subclass of Exp to produce the base class structure for a sub-type of Exp. */
  class BaseClass(expr:Exp) {
    def apply(): CPPClass = {

      val name = expr.getClass.getSimpleName

      new CPPClass(name, s"$name : public Exp", Seq.empty, Seq.empty)
    }

    // semantic type is based on the subclass (i.e., it will be exp('Base, 'Lit) or exp('Base, 'Add)
    val semanticType:Type = exp(exp.base, expr)
  }

  /**
    * Construct class to represent subclass of Exp.
    *
    * @param sub    sub-type of Exp (i.e., Lit) for whom implementation class is synthesized.
    */
  class ImplClass(sub:Exp) {
    def apply(unit:CPPClass): CPPClass = {
      val name = sub.getClass.getSimpleName

      // Builds up the attribute fields and set/get methods. Also prepares for one-line constructor.
      var params:Seq[String] = Seq.empty
      var cons:Seq[String] = Seq.empty

      var addedFields:Seq[String] = Seq.empty
      var addedMethods:Seq[String] = Seq.empty
      var childrenVisit:Seq[String] = Seq.empty

      sub.ops.asScala.foreach {
        case att: Attribute =>
          val capAtt = att.attName.capitalize
          val tpe = Type_toString(att.attType)

          addedFields = addedFields :+ s"const $tpe* ${att.attName}_;"

          // prepare for constructor
          params = params :+ s"const $tpe* ${att.attName}"
          cons = cons :+ s"${att.attName}_(${att.attName})"

          // only if of type Exp
          if (att.attType == Types.Exp) {
            childrenVisit = childrenVisit :+ s"${att.attName}_->Accept(visitor);"
           }

          // make the set/get methods
          addedMethods = addedMethods :+ s"const $tpe* get$capAtt() const { return ${att.attName}_; }"

        case _ =>
      }

      // make constructor
      addedMethods = addedMethods :+ s"${sub.getClass.getSimpleName} (${params.mkString(",")}) : ${cons.mkString(",")} {}"

      val visitor = s"""|void Accept(ExpVisitor* visitor) const {
                        |   ${childrenVisit.mkString("\n")}
                        |   visitor->Visit$name(this);
                        |}
                     """.stripMargin

      addedMethods = addedMethods :+ visitor

      new CPPClass(unit.name, unit.signature, unit.publicArea ++ addedMethods, unit.privateArea ++ addedFields)
    }

    val semanticType:Type = exp(exp.base, sub) =>: exp(exp.visitor,sub)
  }

  /** Brings in classes for each operation. These can only be completed with the implementations. */
  class OpImpl(op:Operation) {
    def apply: CPPClass = {

      val name = op.getClass.getSimpleName
      val tpe = Type_toString(op.`type`)

      //implementations
      val methods:Map[Class[_ <: Exp],String] = getImplementation(op)

      // each visitor stores local values for access
      val base:Seq[String] = Seq(s"std::map<const Exp*, $tpe> value_map_;")

      // need method for accessing these local values
      val accessor:Seq[String] = Seq(
        s"""
           |$tpe getValue(const Exp& e) {
           |  return value_map_[&e];
           |}
         """.stripMargin)

      new CPPClass(name, s"$name : public ExpVisitor", accessor ++ methods.values.toSeq, base)
    }

    val semanticType:Type = ops (ops.visitor,op)
  }

  // sample Driver
  @combinator object Driver {
    def apply: MainClass = {

    val code =
     s"""|int val1 = 1;
         |int val2 = 2;
         |int val3 = 3;
         |Lit one   = Lit(&val1);
         |Lit two   = Lit(&val2);
         |Lit three = Lit(&val3);
         |
         |Add four = Add(&one, &two);
         |Add five = Add(&four, &three);
         |Neg six  = Neg(&five);
         |
         |PrettyP pp;
         |five.Accept(&pp);
         |std::cout << pp.getValue(five) << std::endl;
         |
         |Eval e;
         |five.Accept(&e);
         |std::cout << e.getValue(five) << std::endl;
         |
         |six.Accept(&pp);
         |std::cout << pp.getValue(six) << std::endl;
         |
         |six.Accept(&e);
         |std::cout << e.getValue(six) << std::endl;
         |return 0;""".stripMargin

      new MainClass("Driver", Seq(code))
  }
    val semanticType:Type = driver
  }

  // perhaps this could be auto-computed. But with a little prescience, it makes sense
  // Header
  //  Stubs
  //  Lit
  //  Add
  //  Sub
  //  Eval
  //  Neg
  //  PrettyP
  //  Driver

  class BaseModule(model:DomainModel) {
    def apply(exp:CPPFile, visitor:CPPFile,
              lit:CPPFile, add:CPPFile, sub:CPPFile, neg:CPPFile,
              eval:CPPFile, pp:CPPFile,
              driver:CPPFile):CPPFile = {
      val header:Seq[String] =
        s"""
           |#include <iostream>
           |#include <map>
           |#include <memory>
           |#include <sstream>
           |#include <string>
           |
         """.stripMargin.split("\n")

      // class predefs
      val defs:Seq[String] = model.data.asScala.map(sub => s"class ${sub.getClass.getSimpleName};")
      val data:Seq[String] = Seq(lit, add,sub,neg).map(sub => sub.toString)
      val ops:Seq[String] = Seq(eval, pp).map(op => op.toString)

      new StandAlone("base", header  ++ defs ++ Seq(exp.toString, visitor.toString) ++ data ++ ops ++ Seq(driver.toString))
    }

    val semanticType:Type = generated(generated.visitor) =>: exp(exp.base, new Exp) =>:
      exp(exp.visitor, new Lit) =>:
      exp(exp.visitor, new Add) =>:
      exp(exp.visitor, new Sub) =>:
      exp(exp.visitor, new Neg) =>:
      ops(ops.visitor, new Eval) =>:
      ops(ops.visitor, new PrettyP) =>:
      driver =>:
      module(module.base)
  }

}


