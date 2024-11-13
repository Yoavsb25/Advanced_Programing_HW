import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class NumberGuessingGameTest {

    @Test
    public void testMissingGuessInput() throws IOException {
        Player player = () -> "Test Student";

        NumberGuessingGame game = new NumberGuessingGame();

        StatusUpdate up;

        game.playerJoin(player, 1);
        up = game.playerMove(player, "", 2);

        assertEquals("Move response incorrect", "You must guess a number!", up.getMessages(player).get(0));
    }


    @Test
    public void testBadGuessInput() {
        Player player = () -> "Test Student";

        NumberGuessingGame game = new NumberGuessingGame();

        StatusUpdate up;

        game.playerJoin(player, 1);
        up = game.playerMove(player, "bad", 2);

        assertEquals("Move response incorrect", "You must guess a number!", up.getMessages(player).get(0));
    }


    @Test
    public void testLowGuess()  {
        Player player = () -> "Test Student";

        NumberGuessingGame game = new NumberGuessingGame(50);

        StatusUpdate up;

        game.playerJoin(player, 1);
        up = game.playerMove(player, "25", 2);

        assertEquals("Move response incorrect", "Your guess is too low", up.getMessages(player).get(0));
    }


    @Test
    public void testHighGuess() {
        Player player = () -> "Test Student";

        NumberGuessingGame game = new NumberGuessingGame(50);

        StatusUpdate up;

        game.playerJoin(player, 1);
        up = game.playerMove(player, "75", 2);

        assertEquals("Move response incorrect", "Your guess is too high", up.getMessages(player).get(0));
    }


    @Test
    public void testExactGuess() {
        Player player = () -> "Test Student";

        NumberGuessingGame game = new NumberGuessingGame(50);

        StatusUpdate up;

        game.playerJoin(player, 1);
        up = game.playerMove(player, "50", 2);

        assertEquals("Move response incorrect", "Yay! " + player.getName() + " has guessed the number!", up.getMessages().get(0));
        assertTrue("Game did not end with good guess", game.hasEnded());
    }
}