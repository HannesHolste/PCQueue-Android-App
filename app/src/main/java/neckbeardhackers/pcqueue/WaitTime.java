package neckbeardhackers.pcqueue;

/**
 * Created by brandon on 10/25/15.
 * Meant to easily keep track of wait times
 */
public class WaitTime {
    private int currentWait;

    public WaitTime() {
        this.currentWait = 0;
    }

    public int getCurrentWait() {
        return this.currentWait;
    }

    public void setCurrentWait(int wait) {
        this.currentWait = wait;
    }


}
