package org.combinators.solitaire.giant;

import org.combinators.solitaire.giant.model.*;
import ks.client.gamefactory.GameWindow;
import ks.common.model.*;
import ks.launcher.Main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// Should hold falsified cases
public class GiantTestCases {

    Giant game;

    private Stack getValidStack() {
        Stack movingCards = new Stack();
        for (int rank = Card.KING; rank >= Card.ACE; rank--) {
            movingCards.add(new Card(rank, Card.CLUBS));
        }
        return movingCards;
    }

    private Stack notDescending(Stack stack) {
        if (stack.empty() || stack.count() == 1) {
            stack.add(new Card(Card.KING, Card.CLUBS));
            stack.add(new Card(Card.ACE, Card.CLUBS));
            return stack;
        } else {
            for (int i = 0; i < stack.count() - 1; i++) {
                if (stack.peek(i).getRank() != stack.peek(i + 1).getRank() + 1) {
                    return stack;
                }
            }
            // end of loop, stack in descending order
            Card top = stack.get();
            stack.add(new Card(Card.KING, Card.CLUBS));
            stack.add(top);
            return stack;
        }
    }

    @Before
    public void makeGame() {
        game = new Giant();
        final GameWindow window = Main.generateWindow(game, Deck.OrderBySuit);
        window.setVisible(true);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMoveColumn1() {
        // Testing TableauToEmptyTableau$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        Stack movingCards = new Stack();
        movingCards.add(new Card(Card.JACK, Card.CLUBS));
        Stack source = game.tableau[1];
        Stack destination = game.tableau[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = movingCards.count();
        MoveColumn move = new MoveColumn(source, movingCards, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testMoveColumn2() {
        // Testing TableauToNextTableau$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        game.tableau[2].add(new Card(Card.THREE, Card.CLUBS));
        game.tableau[2].add(new Card(Card.TWO, Card.HEARTS));
        Stack movingCards = new Stack();
        movingCards.add(new Card(Card.ACE, Card.CLUBS));
        Stack source = game.tableau[1];
        Stack destination = game.tableau[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = movingCards.count();
        MoveColumn move = new MoveColumn(source, movingCards, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testBuildFoundation3() {
        // Testing TableauToEmptyFoundation$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.foundation[2].removeAll();
        Stack movingCards = new Stack();
        movingCards.add(new Card(Card.NINE, Card.CLUBS));
        Stack source = game.tableau[1];
        Stack destination = game.foundation[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = movingCards.count();
        BuildFoundation move = new BuildFoundation(source, movingCards, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testBuildFoundation4() {
        // Testing TableauToNextFoundation$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.foundation[2].add(new Card(Card.ACE, Card.CLUBS));
        game.foundation[2].add(new Card(Card.TWO, Card.CLUBS));
        Stack movingCards = new Stack();
        movingCards.add(new Card(Card.THREE, Card.CLUBS));
        Stack source = game.tableau[1];
        Stack destination = game.foundation[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = movingCards.count();
        BuildFoundation move = new BuildFoundation(source, movingCards, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testMoveColumn5() {
        // Testing TableauToTableauMultipleCards$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        game.tableau[2].add(new Card(Card.QUEEN, Card.CLUBS));
        game.tableau[2].add(new Card(Card.JACK, Card.HEARTS));
        Stack movingCards = new Stack();
        movingCards.add(new Card(Card.TEN, Card.CLUBS));
        Stack source = game.tableau[1];
        Stack destination = game.tableau[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = movingCards.count();
        MoveColumn move = new MoveColumn(source, movingCards, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testMoveColumn6() {
        // Testing TableauToEmptyTableauMultipleCards$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        Stack movingCards = new Stack();
        movingCards.add(new Card(Card.THREE, Card.CLUBS));
        movingCards.add(new Card(Card.TWO, Card.DIAMONDS));
        Stack source = game.tableau[1];
        Stack destination = game.tableau[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = movingCards.count();
        MoveColumn move = new MoveColumn(source, movingCards, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }
}
