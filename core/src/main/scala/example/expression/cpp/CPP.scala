package example.expression.cpp

/**
  * Pulled out here because when in CPPSemanticTypes it caused confusion in inhabitation
  *   I.e., Is it "Expression_CPP.this.repository.CPPClass" or "ExpressionSynthesis_CPP.this.CPPClass"
  */

/**
  * Useful constructs for synthesis. Perhaps a poor-man's AST.
  *  class $signature {
  *  public:
  *    $publicArea
  *  private:
  *    $privateArea
  * };
  *
  * Note: name is likely part of $signature, but it is being pulled out so we can name the file after it.
  */
class CPPBase {
  def indent (lines:Seq[String]):String = {
    lines.map(l => s"  ${l}").mkString("\n")
  }

  /** Indent an arbitrary string (with \n as separators). */
 // def indent(s:String):String = indent(s.split("\n"))
}

/** Any CPP artifact that should be placed in a file. */
abstract class CPPFile extends CPPBase {

  /** return name of file. */
  def fileName() : String
}

/**
  * Useful for header files, or forward reference to class def ahead of real class def
  */
final class StandAlone(val _name:String, val _body:Seq[String]) extends CPPFile {
  val body = _body
  val name = _name

  override def toString(): String = body.mkString("\n")

  override def fileName() = name
}

final class MainClass (val _name:String, val _body:Seq[String]) extends CPPFile {
  val body = _body
  val name = _name

  override def toString(): String = s"""|int main() {
                                        |${indent(body)}
                                        |}""".stripMargin

    override def fileName() = name
}

final class CPPClass (val _name:String, _signature:String, val _publicArea:Seq[String], _privateArea:Seq[String]) extends CPPFile {

  val name = _name
  val signature = _signature
  val publicArea = _publicArea
  val privateArea =  _privateArea

  override def fileName() = name

  override def toString(): String = {
    s"""
       |class $signature {
       |public:
       |${indent(publicArea)}
       |
       |private:
       |${indent(privateArea)}
       |};
       """.stripMargin
  }
}



/**
  * Useful constructs for synthesis. Perhaps a poor-man's AST.
  *  $signature {
  *     $body
  *  }
  */
class CPPMethod (val _signature:String, val _body:Seq[String]) extends CPPBase {

  val signature = _signature
  val body = _body

  override def toString(): String = indent(Seq(s"$signature {") ++ body ++ Seq("}"))
}

/**
  * Useful constructs for synthesis. Perhaps a poor-man's AST.
  *  $signature
  */
class CPPField (val _signature:String) extends CPPBase {

  val signature = _signature

  override def toString(): String = indent(Seq(signature))
}
