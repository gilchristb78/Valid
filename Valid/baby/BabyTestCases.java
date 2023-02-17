package org.combinators.solitaire.baby;

import org.combinators.solitaire.baby.model.*;
import ks.client.gamefactory.GameWindow;
import ks.common.model.*;
import ks.launcher.Main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// Should hold falsified cases
public class BabyTestCases {

    Baby game;

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
        game = new Baby();
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
        // Testing SameSuit$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        game.tableau[2].add(new Card(Card.THREE, Card.HEARTS));
        game.tableau[2].add(new Card(Card.TWO, Card.HEARTS));
        Stack movingCards = new Stack();
        movingCards.add(new Card(Card.ACE, Card.HEARTS));
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
        // Testing DifferentSuit$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        game.tableau[2].add(new Card(Card.THREE, Card.HEARTS));
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
    public void testMoveColumn3() {
        // Testing TableauToEmptyTableau$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        Stack movingCards = new Stack();
        movingCards.add(new Card(Card.THREE, Card.CLUBS));
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
    public void testMoveColumn4() {
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
