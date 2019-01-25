package org.combinators.solitaire.domain

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.BodyDeclaration
import org.combinators.templating.twirl.Java

/**
  * Variation-specific element sub-types are allowed.
  *
  * Orientation. By default, vertical downwards.
  *
  * Determines whether view is one card at a time (like Pile) or multiple (like Column, Row).
  */
abstract class Element(
                        val viewOneAtATime: Boolean,
                        val verticalOrientation:Boolean = true,
                        val modelMethods:Seq[BodyDeclaration[_]] = Seq.empty,
                        val viewMethods:Seq[BodyDeclaration[_]] = Seq.empty,
                        val modelImports:Seq[ImportDeclaration] = Seq.empty,
                        val viewImports:Seq[ImportDeclaration] = Seq.empty)
                        {
  // case classes have $ in their name
  def name:String = getClass.getSimpleName.replace("$","")
}

case object Card extends Element(true)

case object Column extends Element(false)
case object Pile extends Element (true)
case object Row extends Element(false, true)
case object BuildablePile extends Element (false)
case class Stock(numDecks:Int = 1) extends Element(true)

/**
  * A FanPile is like the wastepile in Klondike when you deal three cards at a time.
  * @param num  how many cards to present
  */
case class FanPile(num:Int) extends Element (false)
