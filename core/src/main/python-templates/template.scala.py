@(Structure:Python,Hilight:Python,Shufflehook:Python,CreateGameParams:Python,GameID:Python)

# another try
@Structure.indent

#!/usr/bin/env python
# -*- mode: python; coding: utf-8; -*-
##---------------------------------------------------------------------------##
##
## Synthesized from CLS
##    shallHighlightMatch = hilight
##
##    Structure
##      Foundation_Class = SS_FoundationStack
##      RowStack_Class = SuperMoveRK_RowStack
##
##    ShuffleHook
## def _shuffleHook(self, cards):
##        # move Aces to bottom of the Talon (i.e. last cards to be dealt)
##        return self._shuffleHookMoveToBottom(cards, lambda c: (c.rank == 0, c.suit))
##
##    shallHighlightMatch = Game._shallHighlightMatch_RK
##---------------------------------------------------------------------------##

__all__ = []

# imports
import sys

# PySol imports
from pysollib.gamedb import registerGame, GameInfo, GI
from pysollib.util import *
from pysollib.mfxutil import kwdefault
from pysollib.stack import *
from pysollib.game import Game
from pysollib.layout import Layout
from pysollib.pysoltk import MfxCanvasText

# another try
@Structure.indent

# another try
@Structure.indent

# ************************************************************************
# * BeleagueredCastle
# ************************************************************************

class BeleagueredCastle(Game):
@Structure.indent

@Shufflehook.indent

    #
    # game layout
    #
    def createGame(self, @CreateGameParams):
        # create layout
        l, s = Layout(self), self.s

        # set window
        # (set size so that at least 13 cards are fully playable)
        w = max(3*l.XS, l.XS+(playcards-1)*l.XOFFSET)
        x0 = l.XM
        x1 = x0 + w + 2*l.XM
        x2 = x1 + l.XS + 2*l.XM
        x3 = x2 + w + l.XM
        h = l.YM + (4+int(reserves!=0))*l.YS + int(texts)*l.TEXT_HEIGHT
        self.setSize(x3, h)

        # create stacks
        y = l.YM
        if reserves:
            x = x1 - int(l.XS*(reserves-1)/2)
            for i in range(reserves):
                s.reserves.append(ReserveStack(x, y, self))
                x += l.XS
            y += l.YS
        x = x1
        for i in range(4):
            s.foundations.append(self.Foundation_Class(x, y, self, suit=i, max_move=0))
            y += l.YS
        if texts:
            tx, ty, ta, tf = l.getTextAttr(None, "ss")
            tx, ty = x+tx, y-l.YS+ty
            font = self.app.getFont("canvas_default")
            self.texts.info = MfxCanvasText(self.canvas, tx, ty,
                                            anchor=ta, font=font)
        for x in (x0, x2):
            y = l.YM+l.YS*int(reserves!=0)
            for i in range(4):
                stack = self.RowStack_Class(x, y, self)
                stack.CARD_XOFFSET, stack.CARD_YOFFSET = l.XOFFSET, 0
                s.rows.append(stack)
                y += l.YS
        x, y = self.width - l.XS, self.height - l.YS
        s.talon = InitialDealTalonStack(x, y, self)
        if reserves:
            l.setRegion(s.rows[:4], (-999, l.YM+l.YS-l.CH/2, x1-l.CW/2, 999999))
        else:
            l.setRegion(s.rows[:4], (-999, -999, x1-l.CW/2, 999999))

        # default
        l.defaultAll()

    #
    # game overrides
    #
    def startGame(self):
        for i in range(4):
            self.s.talon.dealRow(frames=0)
        self.startDealSample()
        for i in range(2):
            self.s.talon.dealRow()
        self.s.talon.dealRow(rows=self.s.foundations)




# register the game
registerGame(GameInfo(@GameID, BeleagueredCastle, "Beleaguered Castle",
                      GI.GT_1DECK_TYPE:| GI.GT_OPEN, 1, 0, GI.SL_MOSTLY_SKILL))
