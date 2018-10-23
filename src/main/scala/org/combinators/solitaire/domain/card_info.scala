package org.combinators.solitaire.domain

sealed trait Color
case object Red extends Color
case object Black extends Color

sealed trait Suit {
  val color:Color
}

case object Clubs extends Suit { val color:Color = Black }
case object Diamonds extends Suit { val color:Color  = Red }
case object Hearts extends Suit { val color:Color  = Red }
case object Spades extends Suit { val color:Color  = Black }

object suits {
  def all: Seq[Suit] = Seq(Clubs, Diamonds, Hearts, Spades)
}

sealed trait Rank {
  val num:Int
}

case object Ace extends Rank { val num = 1}
case object Two extends Rank { val num = 2}
case object Three extends Rank  { val num = 3}
case object Four extends Rank { val num = 4}
case object Five extends Rank { val num = 5}
case object Six extends Rank { val num = 6}
case object Seven extends Rank { val num = 7}
case object Eight extends Rank { val num = 8}
case object Nine extends Rank { val num = 9}
case object Ten extends Rank { val num = 10}
case object Jack extends Rank { val num = 11}
case object Queen extends Rank { val num = 12}
case object King extends Rank { val num = 13}