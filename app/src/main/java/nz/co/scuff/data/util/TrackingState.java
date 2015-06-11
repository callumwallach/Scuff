package nz.co.scuff.data.util;

/**
 * Created by Callum on 28/04/2015.
 */
public enum TrackingState {

    RECORDING(0), PAUSED(1), COMPLETED(2);

    private final int value;

    private TrackingState(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
