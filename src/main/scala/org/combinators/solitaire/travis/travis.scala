package org.combinators.solitaire.travis

import org.combinators.solitaire
import org.combinators.solitaire.archway._
import org.combinators.solitaire.bakersdozen._



object travisMain extends App {
  println("----------   Archway Testing   ----------")
  archway
  println("----------   bakerdozen Testing   ----------")
  bakersDozen

}

object archway {
  ArchwayMain
}

object bakersDozen {
  BakersDozenMain
  SpanishPatienceMain
  CastlesInSpainMain
}

