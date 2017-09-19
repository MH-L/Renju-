package algorithm;

/**
 * Class for stat object. Win/lose/tie in terms of black.
 */
public class StatObj {
    public int wins;
    public int ties;
    public int losses;

    public StatObj(int wins, int losses, int ties) {
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
    }

    public double getWinRate() {
        return (double) wins / (double) (ties + losses + wins);
    }

    public double getLossRate() {
        return (double) losses / (double) (ties + losses + wins);
    }

    public int getSupport() {
        return wins + ties + losses;
    }
}
