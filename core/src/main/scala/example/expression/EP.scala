package example.expression

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.{ConstructorDeclaration, FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import expression._
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Collect, Neg, PrettyP, Sub}
import expression.operations.SimplifyAdd
import expression.types.Types

import scala.collection.JavaConverters._

/** Use Modularity2016 Java solution. Built from same domain model. */
trait EP extends Base with SemanticTypes {

  /** Add dynamic combinators as needed. */
  override def init[G <: ExpressionDomain](gamma: ReflectedRepository[G], model: DomainModel): ReflectedRepository[G] = {
    var updated = super.init(gamma, model)

    // Every extension needs its own FinalClass
    model.data.asScala.foreach {
      sub:Exp => {
        updated = updated
          .addCombinator (new FinalClass(sub))
      }
    }

    model.ops.asScala.foreach {
      op:Operation => {
        updated = updated
          .addCombinator(new AddOperation(op))
      }
    }

    // implementations of operations: have to be defined. Note that these "raw Scala methods" could be replaced with tabular tool
    //
    //
    //  Eval     x  Lit, Neg, Add, Sub  ...  Mult Divide ...
    //  Print    |  pl,  pn,  pa,  ps
    //  Collect  |  cl,  cn,  ca,  cs   ...
    //  Simplify |  sl,  sn,  sa,  ss   ...

    // Row entries for a given operation as expressed by the different column types
    def registerImpl (op:Operation, fm:FunctionMethod, map:Map[Exp,String]): Unit = {
      map.keys.foreach {
         key =>
          updated = updated
            .addCombinator(new AddDefaultImpl(op, fm, key, Java(map(key)).statements()))
      }
    }

    // note default 'Eval' operation is handled specially since it is assumed to always exist in top Exp class
    registerImpl(new Eval,  new FunctionMethod("eval", Types.Int), Map(
      new Lit -> "return value();",
      new Neg -> "return -exp().eval();",
      new Add -> "return left().eval() + right().eval();",
      new Sub -> "return left().eval() - right().eval();"
    ))

    def registerExtension (op:Operation, map:Map[Exp,String]): Unit = {
      map.keys.foreach {
        key =>
          updated = updated
            .addCombinator(new AddExpOperation(key, op, Java(map(key)).statements()))
      }
    }

    registerExtension(new PrettyP, Map(
      new Lit -> """return "" + value();""",
      new Add -> """return "(" + left().print() + " + " + right().print() + ")";""",
      new Sub -> """return "(" + left().print() + " - " + right().print() + ")";""",
      new Neg -> """return "-" + exp().print();""",
    ))

    updated = updated
        //.addCombinator(new AddExpOperation(new Lit, new PrettyP, Java(s"""return "" + value();""").statements()))
        //.addCombinator(new AddExpOperation(new Add, new PrettyP, Java(s"""return "(" + left().print() + " + " + right().print() + ")";""").statements()))
        //.addCombinator(new AddExpOperation(new Sub, new PrettyP, Java(s"""return "(" + left().print() + " - " + right().print() + ")";""").statements()))
        .addCombinator(new AddExpOperation(new Lit, new Collect, Java(
                s"""
                   |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
                   |list.add(value());
                   |return list;
                 """.stripMargin).statements()))
        .addCombinator(new AddExpOperation(new Add, new Collect, Java(
                s"""
                   |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
                   |list.addAll(left().collectList());
                   |list.addAll(right().collectList());
                   |return list;
                 """.stripMargin).statements()))
      .addCombinator(new AddExpOperation(new Sub, new Collect, Java(
        s"""
           |java.util.List<Integer> list = new java.util.ArrayList<Integer>();
           |list.addAll(left().collectList());
           |list.addAll(right().collectList());
           |return list;
                 """.stripMargin).statements()))

    // combine PrettyPrint and Collect
    //    interface ExpPC extends ExpP, ExpC{}
    //    interface LitPC extends ExpPC, LitP, LitC{}
    //    interface AddPC extends ExpPC, AddP, AddC {
    //      ExpPC e1(); ExpPC e2();
    //    }

    // for each TYPE (i.e., Lit, Add) you need to define interfaces.

    // Add relevant combinators to construct the sub-type classes, based on domain model.
      model.data.asScala.foreach {
        sub:Exp => {
          updated = updated
            .addCombinator (new SubInterface(sub))
        }
      }

    updated
  }





  /**

    interface Exp { int eval(); }

    class Lit implements Exp {
      int x;
      Lit(int x) { this.x = x; }
      public int eval() { return x; }
    }

    abstract class Add implements Exp {
      abstract Exp getE1();
      abstract Exp getE2();
      public int eval() {
        return getE1().eval + getE2().eval;
      }
    }

    ADD OPERATION
    interface ExpP extends Exp { String print(); }

    class LitP extends Lit implements ExpP {
      LitP(int x) { super(x); }
      public String print() { return "" + x; }
    }

    abstract AddP extends Add implements ExpP {
      abstract ExpP getE1();  // refined
      abstract ExpP getE2();  // refined
      public String print() {
        return "(" + getE1().print() + " + " + getE2().print() + ")";
      }
    }
    class AddFinal extends Add {
      Exp e1, e2;
      AddFinal(Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
      }
      Exp getE1() { return e1; }
      Exp getE2() { return e2; }
    }



    *
    *
    */


  /**
    * Construct class to represent subclass of Exp.
    *
    *  interface Lit extends Exp {
    *    int x();
    *    default int eval() { return x(); }
    *  }
    *
    *  but also recursive types:
    *
    *  interface Add extends Exp {
    *     Exp e1(); Exp e2();
    *     default int eval() {
    *      return e1().eval() + e2().eval();   $IMPLEMENT[
    *     }
    *   }
    *
    *  addImpl(new Eval, new Add, Java(s"""return e1().eval() + e2().eval();""").statements())
    *  addImpl(new Eval, new Lit, Java(s"""return x();""").statements())
    *
    * @param sub     Exp subclass whose interface we are generating
    */
  class SubInterface(sub:Exp) {
      def apply(): CompilationUnit = {
        val name = sub.getClass.getSimpleName

        val unit = Java (
          s"""
             |package ep;
             |public interface $name extends Exp {}
       """.stripMargin).compilationUnit()

        sub.ops.asScala.foreach {
          case att: Attribute => {
            val tpe = Type_toString(att.attType)

            val fields:Seq[MethodDeclaration] = Java(s"""$tpe ${att.attName}();""")
              .classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

            fields.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

          }
          case func:FunctionMethod => {

          }
        }

        unit
      }

    val semanticType:Type = ep(ep.interface, sub)
  }

  /**
    * i.e., LitFinal
    *
    * public class LitFinal implements Lit {
    *   Integer value;
    *   public LitFinal(int value) { this.value = value; }
    *   public Integer value() { return value; }
    * }
    *
    * @param sub
    */
  class FinalClass(sub:Exp) {
    def apply(): CompilationUnit = {
      val name = sub.getClass.getSimpleName

      val unit = Java (
        s"""
           |package ep;
           |public class ${name}Final implements $name {}
       """.stripMargin).compilationUnit()

      var params:Seq[String] = Seq.empty
      var cons:Seq[String] = Seq.empty

      sub.ops.asScala.foreach {
        case att: Attribute => {
          val tpe = Type_toString(att.attType)

          val fields:Seq[FieldDeclaration] = Java(s"""
                       |private $tpe ${att.attName};
                       |""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
          fields.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

          // prepare for constructor
          params = params :+ s"$tpe ${att.attName}"
          cons   = cons   :+ s"  this.${att.attName} = ${att.attName};"

          val methods:Seq[MethodDeclaration] = Java(
            s"""
               |public $tpe ${att.attName}() { return ${att.attName};}
              """.stripMargin)
            .classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

          methods.foreach { x => unit.getTypes.get(0).getMembers.add(x) }



        }
        case func:FunctionMethod => {

        }
      }

      // Builds up the attribute fields and set/get methods. Also prepares for one-line constructor.

      // make constructor
      val constructor = Java(
        s"""
           |public ${sub.getClass.getSimpleName}Final (${params.mkString(",")}) {
           |   ${cons.mkString("\n")}
           |}""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[ConstructorDeclaration]).head

      unit.getTypes.get(0).getMembers.add(constructor)

      unit
    }

    val semanticType:Type = ep(ep.finalType, sub)
  }

  /**
    * Given an interface for a type, adds a default implementation of given operation
    *
    * @param op
    * @param fm
    * @param sub
    * @param stmts
    */
  class AddDefaultImpl(op:Operation, fm:FunctionMethod, sub:Exp, stmts:Seq[Statement]) {
    def apply(unit:CompilationUnit): CompilationUnit = {

      val tpe = Type_toString(fm.returnType)
      val name = fm.name

        val fields:Seq[MethodDeclaration] = Java(
          s"""
             |/* default */ $tpe $name() {
             |    ${stmts.mkString("\n")}
             |}
           """.stripMargin)
          .classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

        fields.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

      unit
    }

    val semanticType:Type = ep(ep.interface, sub) =>: ep(ep.defaultMethods, sub, op)
  }

  /**
    * Merging two Exp subclasses, such as PrettyPrint and Collect, yield:
    *
    * interface ExpPC extends ExpPrettyP, ExpCollect{}
    * interface LitPC extends ExpPrettyPCollect, LitPrettyP, LitCollect{}
    * interface AddPC extends ExpPrettyPCollect, AddPrettyP, AddCollect {
    *   ExpPC e1(); ExpPC e2();
    * }
    *
    * And these are completed by Final classes...
    *
    * class AddPCFinal implements AddPC { ... }
    */
//  class Combined(one:Exp, two:Exp, op:Operation) {
//    def apply(unit:CompilationUnit): CompilationUnit = {
//
//      val tpe = Type_toString(fm.returnType)
//      val name = fm.name
//
//      val primary = Java(
//        s"""
//           |/* default */ $tpe $name() {
//           |    ${stmts.mkString("\n")}
//           |}
//           """.stripMargin).compilationUnit()
//
//      fields.foreach { x => unit.getTypes.get(0).getMembers.add(x) }
//
//      unit
//    }
//
//    val semanticType:Type = ep(ep.interface, two, op) =>: architecture(one, two)
//  }

  /**
    * Given an extension to Exp and a given operation (and its stmts implementation) produce an
    * interface with default method. Overide methods that are of class Exp. Thus: AddExpOperation (Add, PrettyP, ...)
    *
    * interface AddPrettyP extends Add, PrettyP {
    *    PrettyP left();
    *    PrettyP right();
    *    default String print() {
    *      return "(" + left().print() + " + " + right().print() + ")";
    *    }
    * }
    *
    * @param exp
    * @param op
    * @param stmts
    */
  class AddExpOperation(exp:Exp, op: Operation, stmts:Seq[Statement]) {
    def apply() : CompilationUnit = {
      val opName = op.getClass.getSimpleName
      val expName = exp.getClass.getSimpleName

      val unit:CompilationUnit = Java(s"""
           |package ep;
           |interface $expName$opName extends $expName, $opName { }
           |""".stripMargin).compilationUnit()

      val tpe = Type_toString(op.`type`)

      val methods:Seq[MethodDeclaration] = Java(
        s"""
           |/* default */ $tpe ${op.name}() {
           |   ${stmts.mkString("\n")}
           |}
         """.stripMargin)
        .classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

      // reclassify an field of type Exp with the more precise $expName
      // PrettyP left();
      // PrettyP right();
      exp.ops.asScala.foreach {
        case att: Attribute => {
          // only redefine if originally the Exp field.
          if (att.attType == Types.Exp) {
            val fields: Seq[MethodDeclaration] = Java(s"""$opName ${att.attName}();""")
              .classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

            fields.foreach { x => unit.getTypes.get(0).getMembers.add(x) }
          }
        }
        case func:FunctionMethod => {

        }
      }


      methods.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

      unit
    }

    val semanticType:Type = ep(ep.interface, exp, op)
  }

 // interface ExpP extends Exp { String print(); }
  class AddOperation(op: Operation) {
   def apply() : CompilationUnit = {
     val name = op.getClass.getSimpleName

     val unit:CompilationUnit = Java(s"""
              |package ep;
              |interface $name extends Exp { }
              |""".stripMargin).compilationUnit()

     val tpe = Type_toString(op.`type`)

       val methods:Seq[MethodDeclaration] = Java(s"""$tpe ${op.name}();""")
         .classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

       methods.foreach { x => unit.getTypes.get(0).getMembers.add(x) }

     unit
   }

   val semanticType:Type = ep(ep.interface, op)
 }


  /** Generate from domain. */
  @combinator object BaseExpInterface {

    val exp:Exp = new Exp

    def apply() : CompilationUnit = {
      val unit:CompilationUnit = Java(s"""
            |package ep;
            |interface Exp { }
            |""".stripMargin).compilationUnit()

      exp.ops.asScala.foreach {
        case func:FunctionMethod => {
          val tpe = Type_toString(func.returnType)

          val methods:Seq[MethodDeclaration] = Java(s"""$tpe ${func.name}();""")
            .classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])

          methods.foreach { x => unit.getTypes.get(0).getMembers.add(x) }
        }
        case _ => { }
        }

      unit
      }

    val semanticType:Type = ep(ep.interface, new Exp)
  }

  // sample Driver
  @combinator object Driver {
    def apply:CompilationUnit = Java(
      s"""
         |package ep;
         |
         |public class Driver {
         |	public static void main(String[] args) {
         |		System.out.println("======Add======");
         |        Add add = new AddFinal(new LitFinal(7), new LitFinal(4));
         |        System.out.println(add.eval());
         |
 |        System.out.println("======Sub======");
         |        Sub sub = new SubFinal(new LitFinal(7), new LitFinal(4));
         |        System.out.println(sub.eval());
         |
 |        System.out.println("======Print======");
         |        /* the line below causes compile-time error, if now commented out. */
         |        // AddPFinal exp = new AddPFinal(new Lit(7)), new Lit(4));
         |        AddPFinal prt = new AddPFinal(new LitPFinal(7), new LitPFinal(4));
         |        System.out.println(prt.print() + " = " + prt.eval());
         |
 |        System.out.println("======CollectLiterals======");
         |        AddCFinal addc = new AddCFinal(new LitCFinal(3), new LitCFinal(4));
         |        System.out.println(addc.collectLit().toString());
         |
 |        System.out.println("======Composition: Independent Extensibility======");
         |        AddPCFinal addpc = new AddPCFinal(new LitPCFinal(3), new LitPCFinal(4));
         |        System.out.println(addpc.print() + " = " + addpc.eval() + " Literals: " + addpc.collectLit().toString());
         |  }
         |}""".stripMargin).compilationUnit()

    val semanticType:Type = driver
  }

}


