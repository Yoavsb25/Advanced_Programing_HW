

public class NumberGuessingGame implements Game {
    int number;

    boolean wasGuessed;

    /**
     * Initialize with a fixed number.
     *
     * @param number Number to guess
     */
    public NumberGuessingGame(int number) {
        this.number = number;
        wasGuessed = false;
    }

    /**
     * Initialize with a random number.
     * <p>
     * Chooses a random number between 0 and 99
     */
    public NumberGuessingGame() {
        this((int) (Math.random() * 100));
    }

    @Override
    public StatusUpdate playerJoin(Player player, long time) {
        StatusUpdate update = new StatusUpdate();
        update.addMessage(player, "Hello " + player.getName() + ", guess a number between 1 and 100!");
        return update;
    }

    @Override
    public StatusUpdate playerMove(Player player, String word, long time) {
        StatusUpdate update = new StatusUpdate();
        try {
            int guess = Integer.parseInt(word);
            if (guess == number) {
                update.addMessage("Yay! " + player.getName() + " has guessed the number!");
                wasGuessed = true;
            } else if (guess > number) {
                update.addMessage(player, "Your guess is too high");
            } else {
                update.addMessage(player, "Your guess is too low");
            }
        }catch (NumberFormatException nfe) {
            update.addMessage("You must guess a number!");
        }
        return update;
    }

    @Override
    public StatusUpdate playerAbort(Player player, long time) {
        // Do nothing, we don't really care.
        return null;
    }


    @Override
    public boolean hasEnded() {
        return wasGuessed;
    }

}
