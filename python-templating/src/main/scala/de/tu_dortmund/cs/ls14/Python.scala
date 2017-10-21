package de.tu_dortmund.cs.ls14.twirl

import org.apache.commons.lang3.StringEscapeUtils
import play.twirl.api.{BufferedContent, Format, Formats}

import scala.collection.immutable


/**
  * A Python fragment.
  */
class Python private(elements: immutable.Seq[Python], text: String) extends BufferedContent[Python](elements, text) {
  def this(text: String) = this(Nil, Formats.safe(text))
  def this(elements: immutable.Seq[Python]) = this(elements, "")

  private lazy val fullText: String = (text +: elements).mkString

  /**
    * Content type of Python
    */
  val contentType = "text/x-python"

  def indent: Python = {
    Python(fullText.lines.map(l => s"    ${l}").mkString("\n"))
  }

  def getCode: String = fullText
}

/**
  * Helper for Python utility methods.
  */
object Python {
  /**
    * Creates a Python fragment with initial content specified.
    */
  def apply(text: String): Python = {
    new Python(text)
  }

  /**
    * Creates a Python fragment with initial content from the given `text` separated by `separator`.
    */
  def apply(text: Seq[String], separator: String = ";"): Python = {
    apply(text.mkString(separator))
  }
}

object PythonFormat extends Format[Python] {
  /**
    * Integrate `text` without performing any escaping process.
    * @param text Text to integrate
    */
  def raw(text: String): Python = Python(text)

  /**
    * Escapes `text` using Python String rules.
    * @param text Text to integrate
    */
  def escape(text: String): Python = Python(StringEscapeUtils.escapeJava(text))

  /**
    * Generate an empty Python fragment
    */
  val empty: Python = new Python("")

  /**
    * Create an Python Fragment that holds other fragments.
    */
  def fill(elements: immutable.Seq[Python]): Python = new Python(elements)

}

