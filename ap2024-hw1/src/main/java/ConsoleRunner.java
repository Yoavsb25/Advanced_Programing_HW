import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * A class that runs a game with input/output on the system console.
 *
 * @author talm
 */

public class ConsoleRunner {
    Game game;
    PrintStream out;
    PrintStream err;
    BufferedReader in;

    /**
     * Create a new consolerunner with supplied input, output and error streams.
     *
     * @param game
     * @param in
     * @param out
     * @param err
     */
    public ConsoleRunner(Game game, BufferedReader in, PrintStream out, PrintStream err) {
        this.game = game;
        this.in = in;
        this.out = out;
        this.err = err;
    }

    /**
     * Create a new consolerunner using standard input, output and error.
     *
     * @param game
     */
    public ConsoleRunner(Game game) {
        this(game, new BufferedReader(new InputStreamReader(System.in)), System.out, System.err);
    }

    public void runGame() {

        StatusUpdate update;

        String numString;
        int NUM_PLAYERS;

        out.println("How many players? ");

        try {
            numString = in.readLine();
            NUM_PLAYERS = Integer.parseInt(numString);
        } catch (IOException e) {
            // can't do anything!
            err.println(e);
            return;
        } catch (NumberFormatException nfe) {
            return;
        }

        Player[] players = new Player[NUM_PLAYERS];

        for (int i = 0; i < NUM_PLAYERS; ++i) {
            try {
                outputSingleMessage("player " + i, "What's your name? ");
                final String name = in.readLine();

                // Anonymous inner class can access final variables in enclosing scope.
                players[i] = new Player() {
                    @Override
                    public String getName() {
                        return name;
                    }
                };

                update = game.playerJoin(players[i], System.currentTimeMillis());
                // Print the status message.
                outputStatusMessages(update);
            } catch (IOException e) {
                // This should never happen...
                err.println(e);
                return;
            }
        }


        while (!game.hasEnded()) {
            for (int i = 0; i < NUM_PLAYERS; ++i) {
                outputSingleMessage(players[i], "enter your guess-> ");
                try {
                    String word = in.readLine();
                    update = game.playerMove(players[i], word,
                            System.currentTimeMillis());

                    outputStatusMessages(update);
                } catch (IOException e) {
                    err.println(e);
                }
            }
        }

        out.println("Game Over!");

    }

    static final String MSG_SEP = "\n         ";
    static final String MSG_FORMAT = "[For %s] %s"; // Expects to get two params: player name and message


    private void outputSingleMessage(String playerName, String msg) {
        out.println(String.format(MSG_FORMAT, playerName, msg));
    }

    private void outputSingleMessage(Player player, String msg) {
        outputSingleMessage(player.getName(), msg);
    }

    /**
     * Output a status message
     *
     * @param status
     */
    private void outputStatusMessages(StatusUpdate status) {
        Map<Player, List<String>> specifics = status.getSpecificMessages();
        if (specifics != null) {
            for (Player player : specifics.keySet()) {
                List<String> messages = specifics.get(player);
                if (messages != null) {
                    outputSingleMessage(player, String.join(MSG_SEP, messages));
                }
            }
        }
        List<String> defaultMessages = status.getMessages();
        if (defaultMessages != null) {
            outputSingleMessage("everyone", String.join(MSG_SEP, defaultMessages));
        }
    }

}
