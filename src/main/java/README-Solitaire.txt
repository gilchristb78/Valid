// Note: Separately provide Java-based domain model.
Java:CODE


trait Game extends GameTemplate {

  val tableauType = Variable("TableauType")

  val kinding = Kinding(tableauType)
                  .addOption('EightColumnTableau)
                  .addOption('FourColumnTableau)

  // extend existing kinding
  val newKinding = Game.kinding.merge(Kinding(Game.tableauType)
                        .addOption('FiveColumnTableau))

  @combinator object AddEightColumnTableau {
    def apply(s:Solitaire, t:Tableau): Solitaire = {
      s.setTableau(t)
    }
    val semanticType: Type =
        ('P(alpha :&: 'NoTableau) =>: 'ValidTableau =>: 'P(alpha :&: 'Tableau)) :&:
	('P('NoTableau) =>: 'EightColumnTableau =>: 'P('EightColumnTableau)) :&:
	('P('NoTableau) =>: 'FourColumnTableau =>: 'P('FourColumnTableau))
  }

//   Seek: Solitaire ^ P(Tableau)
  lazy val results =
    Results
      .add(Gamma.inhabit[Solitaire]('P(EightColumnTableau :&: FourPileReserve)))

  results.generate()
  val iter = results.interpretedTerms.value.flatMap(_._2).iterator.asJava
  DirectoryMaker.parseResults(iter)




  @combinator object AddTableau {
    def apply(s:Solitaire, t:Tableau): Solitaire = {
      s.setTableau(t)
    }
    val semanticType: Type =
        ('P(alpha :&: 'NoTableau) =>: 'ValidTableau =>: 'P(alpha :&: 'Tableau)) :&:
	('P('NoTableau) =>: tableauType =>: 'P(tableauType)) 
  }




}

Domain model:
  Only goes as deep as needed based on needs and available documentation.
  Product line might have automation needs.
  

Structure of Domain Model for Solitaire
---------------------------------------
class Solitaire { 
  generate   : CompilationUnit   '' use 
  tableau    : Tableau [0..1]
  foundation : Foundation [0..1]
  reserve    : Reserve [0..1]
  waste      : Waste [0..1]
  moves      : Move [*]
  state      : State [0..1]       '' game state beyond structural
  win        : WinLogic 
  layout     : LayoutLogic
}

Advice: How to know what is included in the domain model and how to
delegate/defer the specific logic into the moves and the subsequent
generated/synthesized classes.

This domain is most likely to be stable across all variations. Any
composition here is generic and productive (also: most likely
maximally reusable).

There is a strong sympathetic connection with the underlying
framework, but it doesn't have to be this way. This will help govern
composition but won't interfere with the code. And you won't have to
be held back by limitations in the underlying framework.

Noteworthy: Necessary first step. Designer completes first part, but
then leaves atomic terminals whereby programmer will have to complete
this in code/templates.  Stands in contrast with
Model-Driven-Approach, which presuppose in advance that the initial
modeling tools/technique will be used until completion within the same
notational details. Automatically derive interfaces from
raw-legacy-code. Trying to impose structure on legacy code that
already exists.

Rapid Prototyping start which would generate code.

Eclipse plugin. Refactoring interface.

FreeCell: build a tableau from 8 columns where first four get 7 cards
and next 4 get 6 cards.  Provide reserve of four piles and a
foundation of four piles. Win game when each foundation pile has 13
cards.

Layout: Place Tableau in South and Reserve in NW and Foundations in
NE. Can use coordinates

AvailableMoves:

//              Rel
//             FP HP C     Click   Dbl-click
//        FP   .  .  .      x       x
// Press  HP   x  x  x      x       x
//        C    .  .  .      .       x
//
// (the 5% left)
//
// The Logic primitives: Seq[Statement] --> Return True/False
class LayoutLogic {
  .. map each of the above elements into (x,y) and [width/height] regions
}

// domain-specific elements which are provided "as is" in Java.
class PyramidPile extends Element

// default provided ones
class Card extends Element
class Column extends Element
class Pile extends Element
class BuildablePile extends Element
class Deck extends Element

class HomePile extends Pile
class FreePile extends Pile

class Reserve {
  int size
  piles : FreePile[4]
}

class Foundation {
  int size
  piles : HomePile[4]
  bases : Card[4]
}

class Tableau {
  int size
  columns : Column [8]
}

----------------------
 1. Instantiate model

  use inhabitation to synthesize
  Seek: Solitaire ^ P(Tableau)
  Seek: Solitaire ^ P(EightColumnTableau) ^ P(FourPileReserve) ^ P(FourPileFoundation)
  
    .. step 1 ..



addTableau: ...
  (Solitaire -> Tableau -> Solitaire)                          '' pass through
^ (P(alpha^NoTableau) -> ValidTableau -> P(alpha ^ Tableau)    '' generic
^ (P(NoTableau) -> EightColumns -> P(EightColumnsTableau)      '' specific

add taxonomy

----------------------

// Automatically GenerateThese...
FreeCellPileToFreeCellPileValidationLogic extends ValidationLogic {
 --> template
}

abstract class ValidationLogic { }
abstract class MoveLogic { }
abstract class UndoLogic { }

class Move {
  src    : Element
  moving : Element
  target : Element

  isSingleCard    : boolean    '' awkward
  isMultipleCards : boolean

  valid : ValidationLogic
  undo  : UndoLogic
  move  : MoveLogic
}


abstract class State { }

abstract class WinLogic { }