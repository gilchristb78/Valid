package org.combinators.solitaire.fanfreepile;

import org.combinators.solitaire.fanfreepile.model.*;
import ks.client.gamefactory.GameWindow;
import ks.common.model.*;
import ks.launcher.Main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// Should hold falsified cases
public class FanFreePileTestCases {

    FanFreePile game;

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
        game = new FanFreePile();
        final GameWindow window = Main.generateWindow(game, Deck.OrderBySuit);
        window.setVisible(true);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMoveCard1() {
        // Testing TableauToEmptyTableau$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        Card movingCard = new Card(Card.KING, Card.CLUBS);
        Stack source = game.tableau[1];
        Stack destination = game.tableau[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        MoveCard move = new MoveCard(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testMoveCard2() {
        // Testing TableauToNextTableau$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        game.tableau[2].add(new Card(Card.THREE, Card.CLUBS));
        game.tableau[2].add(new Card(Card.TWO, Card.CLUBS));
        Card movingCard = new Card(Card.ACE, Card.CLUBS);
        Stack source = game.tableau[1];
        Stack destination = game.tableau[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        MoveCard move = new MoveCard(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testMoveCardFoundation3() {
        // Testing TableauToEmptyFoundation$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.foundation[2].removeAll();
        Card movingCard = new Card(Card.ACE, Card.CLUBS);
        Stack source = game.tableau[1];
        Stack destination = game.foundation[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        MoveCardFoundation move = new MoveCardFoundation(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testMoveCardFoundation4() {
        // Testing TableauToNextFoundation$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.foundation[2].add(new Card(Card.ACE, Card.CLUBS));
        game.foundation[2].add(new Card(Card.TWO, Card.CLUBS));
        Card movingCard = new Card(Card.THREE, Card.CLUBS);
        Stack source = game.tableau[1];
        Stack destination = game.foundation[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        MoveCardFoundation move = new MoveCardFoundation(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testTableauToReserve5() {
        // Testing TableauToEmptyReserve$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        Card movingCard = new Card(Card.THREE, Card.CLUBS);
        Stack source = game.tableau[1];
        Stack destination = game.reserve[0];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        TableauToReserve move = new TableauToReserve(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testReserveToReserve6() {
        // Testing ReserveToReserve$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.reserve[1].removeAll();
        Card movingCard = new Card(Card.THREE, Card.CLUBS);
        Stack source = game.reserve[1];
        Stack destination = game.reserve[0];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        ReserveToReserve move = new ReserveToReserve(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testReserveToTableau7() {
        // Testing ReserveToEmptyTableau$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.reserve[1].removeAll();
        game.tableau[0].removeAll();
        Card movingCard = new Card(Card.KING, Card.CLUBS);
        Stack source = game.reserve[1];
        Stack destination = game.tableau[0];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        ReserveToTableau move = new ReserveToTableau(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testReserveToTableau8() {
        // Testing ReserveToNextTableau$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.reserve[1].removeAll();
        game.tableau[0].removeAll();
        game.tableau[0].add(new Card(Card.TWO, Card.CLUBS));
        Card movingCard = new Card(Card.ACE, Card.CLUBS);
        Stack source = game.reserve[1];
        Stack destination = game.tableau[0];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        ReserveToTableau move = new ReserveToTableau(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testReserveToFoundation9() {
        // Testing ReserveToEmptyFoundation$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.reserve[1].removeAll();
        Card movingCard = new Card(Card.ACE, Card.CLUBS);
        Stack source = game.reserve[1];
        Stack destination = game.foundation[0];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        ReserveToFoundation move = new ReserveToFoundation(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }

    @Test
    public void testReserveToFoundation10() {
        // Testing ReserveToNextFoundation$
        String type = "SingleCard$";
        // this is where test set-up must go.
        game.reserve[1].removeAll();
        game.foundation[0].add(new Card(Card.ACE, Card.CLUBS));
        Card movingCard = new Card(Card.TWO, Card.CLUBS);
        Stack source = game.reserve[1];
        Stack destination = game.foundation[0];
        int ss = source.count();
        int ds = destination.count();
        int ms = 1;
        ReserveToFoundation move = new ReserveToFoundation(source, movingCard, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }
}
