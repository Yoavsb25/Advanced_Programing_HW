import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class ConsoleRunnerTest {
    @Test
    public void testMissingNumPlayersInput() {
        MockGame game = new MockGame(3);

        Reader fakeInput = new CharArrayReader("\n\n1\nTest Student\n1\n2\n3\n4\n".toCharArray());

        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        PrintStream err = new PrintStream(new ByteArrayOutputStream());

        ConsoleRunner runner = new ConsoleRunner(game, new BufferedReader(fakeInput), out, err);

        runner.runGame();
    }


    @Test
    public void testMissingGuessInput() throws IOException {
        MockGame game = new MockGame(3);

        Reader fakeInput = new CharArrayReader("1\nTest Student\n\n20\n80\n45\n50\n".toCharArray());

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);
        PrintStream err = new PrintStream(new ByteArrayOutputStream());

        ConsoleRunner runner = new ConsoleRunner(game, new BufferedReader(fakeInput), out, err);
        runner.runGame();

        BufferedReader runOutput = new BufferedReader(new CharArrayReader(outBytes.toString().toCharArray()));


        String[] expectedLines = {
                "How many players?",
                "[For player 0] What's your name?",
                "[For everyone] Test Student joined",
                "[For Test Student] enter your guess->",
                "[For Test Student]",
                "[For Test Student] enter your guess->",
                "[For Test Student] 20",
                "[For Test Student] enter your guess->",
                "[For Test Student] 80",
                "Game Over!"
        };

        for (String expectedLine : expectedLines) {
            String actualLine = runOutput.readLine();
            assertEquals("Console output isn't as expected", expectedLine, actualLine.trim());

        }
    }

    @Test
    public void testOnePlayerGuessOk() throws Exception {
        MockGame game = new MockGame(3);

        Reader fakeInput = new CharArrayReader("1\nTest Student\n20\n80\n45\n50\n".toCharArray());

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);
        PrintStream err = new PrintStream(new ByteArrayOutputStream());

        ConsoleRunner runner = new ConsoleRunner(game, new BufferedReader(fakeInput), out, err);
        runner.runGame();

        BufferedReader runOutput = new BufferedReader(new CharArrayReader(outBytes.toString().toCharArray()));


        String[] expectedLines = {
                "How many players?",
                "[For player 0] What's your name?",
                "[For everyone] Test Student joined",
                "[For Test Student] enter your guess->",
                "[For Test Student] 20",
                "[For Test Student] enter your guess->",
                "[For Test Student] 80",
                "[For Test Student] enter your guess->",
                "[For Test Student] 45",
                "Game Over!"
        };

        for (String expectedLine : expectedLines) {
            String actualLine = runOutput.readLine();
            assertEquals("Console output isn't as expected", expectedLine, actualLine.trim());

        }

    }

    @Test
    public void testMultiPlayerGuessOk() throws Exception {
        int numMoves = 20;

        MockGame game = new MockGame(numMoves);


        String[] fakeNames = {
                "Test Student",
                "Another One",
                "Yet Again",
                "Final Check"
        };

        // Prepare mock input
        ByteArrayOutputStream fakeInputBytes = new ByteArrayOutputStream();
        PrintStream fakeInputPrinter = new PrintStream(fakeInputBytes);

        fakeInputPrinter.println(fakeNames.length); // Number of players
        for (String name : fakeNames) {
            fakeInputPrinter.println(name); // Enter name
        }

        for (int i = 0; i < numMoves + 1; ++i) {
            fakeInputPrinter.println(i * 97 % 100); // "random" move
        }


        Reader fakeInput = new CharArrayReader(fakeInputBytes.toString().toCharArray());

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);
        PrintStream err = new PrintStream(new ByteArrayOutputStream());

        ConsoleRunner runner = new ConsoleRunner(game, new BufferedReader(fakeInput), out, err);
        runner.runGame();

        BufferedReader runOutput = new BufferedReader(new CharArrayReader(outBytes.toString().toCharArray()));

        assertEquals("Console output isn't as expected", "How many players?", runOutput.readLine().trim());
        int i = 0;
        for (String name : fakeNames) {
            assertEquals("Console output isn't as expected", String.format("[For player %d] What's your name?", i), runOutput.readLine().trim());
            assertEquals("Console output isn't as expected", String.format("[For everyone] %s joined", name), runOutput.readLine().trim());
            i++;
        }
        for (i = 0; i < numMoves; ++i) {
            assertEquals("Console output isn't as expected", String.format("[For %s] enter your guess->", fakeNames[i % fakeNames.length]), runOutput.readLine().trim());
            assertEquals("Console output isn't as expected", String.format("[For %s] %d", fakeNames[i % fakeNames.length], i * 97 % 100), runOutput.readLine().trim());
        }

        assertEquals("Console output isn't as expected", "Game Over!", runOutput.readLine().trim());
    }
}