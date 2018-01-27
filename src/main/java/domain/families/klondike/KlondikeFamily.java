package domain.families.klondike;


/**
 * Variations to be supported (taken from PySolFC variations)

 Klondike
 CasinoKlondike
 VegasKlondike
 KlondikeByThrees
 ThumbAndPouch
 Whitehead
 SmallHarp
 Eastcliff
 Easthaven
 Westcliff
 Westhaven
 PasSeul
 BlindAlleys
 Somerset
 Canister
 AgnesSorel
 EightTimesEight
 AchtmalAcht
 Batsford
 Stonewall
 FlowerGarden
 KingAlbert
 Raglan
 Brigade
 Jane
 AgnesBernauer
 Phoenix
 Jumbo
 OpenJumbo
 Lanes
 ThirtySix
 Q_C_
 NorthwestTerritory
 Morehead
 Senate
 SenatePlus
 Arizona
 AuntMary
 DoubleDot
 SevenDevils
 DoubleEasthaven
 TripleEasthaven
 MovingLeft
 Souter
 BigForty
 AliBaba
 Cassim
 Saratoga
 Whitehorse
 Boost
 ArticGarden
 GoldRush
 Usk
 BatsfordAgain
 GoldMine
 LuckyThirteen
 LuckyPiles
 AmericanCanister
 BritishCanister
 Legion
 QueenVictoria
 BigBertha
 Athena
 Chinaman
 EightByEight
 Kingsley
 Scarp
 EightSages



 Most variations for Klondike are concerned with initial layout, parameterized rules and individual allowed rules.

 For example, "King Albert" (from above list) which has
    1. Nine columns of cards (all face up) with 1, 2, 3, 4, 5, ... 9 cards; stock is entirely dealt out
       four foundations piles are still there to build. There are seven reserve cards on the side to choose
       from, laid out in a 2/2/2/1 pattern
       Any card can be moved to an empty column. Can only move one card at a time from the tableau

    2. Raglan extends "King Albert" to make sure the first four ACES are dealt to the foundation, and
       this means that the 8th and 9th column only have eight cards apiece; and there are six cards
       in reserve laid out in a 2/2/2 pattern.

    3. Brigade extends Raglan by having seven columns of five face up cards, four aces are initially
       dealt to the foundation; there are 13 cards in reserve, laid out in a 4/4/4/1 pattern

 In PySol, the Raglan variation is specified as follows, and we want to be as concise, if we can

             class Raglan(KingAlbert):
                RESERVES = (2, 2, 2)

                 def _shuffleHook(self, cards):
                     # move Aces to bottom of the Talon (i.e. last cards to be dealt)
                     return self._shuffleHookMoveToBottom(cards, lambda c: (c.rank == 0, c.suit))

                 def startGame(self):
                     for i in range(6):
                         self.s.talon.dealRow(rows=self.s.rows[i:], frames=0)
                     self.startDealSample()
                     self.s.talon.dealRow(rows=self.s.rows[6:])
                     self.s.talon.dealRow(rows=self.s.reserves)
                     self.s.talon.dealRow(rows=self.s.foundations

 */
public class KlondikeFamily  {

}
