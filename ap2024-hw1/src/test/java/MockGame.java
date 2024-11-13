/**
 * A mock game that has a fixed number of moves, and just echoes its inputs.
 */

public class MockGame implements Game {
    int numMoves;
    int maxMoves;


    public MockGame(int maxMoves) {
        this.numMoves = 0;
        this.maxMoves = maxMoves;
    }

    @Override
    public StatusUpdate playerJoin(Player player, long time) {
        return new StatusUpdate().addMessage(player.getName() + " joined").addMessage(player, null);
    }

    @Override
    public StatusUpdate playerMove(Player player, String word, long time) {
        ++numMoves;
        return new StatusUpdate().addMessage(player, word);
    }

    @Override
    public StatusUpdate playerAbort(Player player, long time) {
        return new StatusUpdate().addMessage(player.getName() + "aborted").addMessage(player, null);
    }

    @Override
    public boolean hasEnded() {
        return numMoves >= maxMoves;
    }
}
