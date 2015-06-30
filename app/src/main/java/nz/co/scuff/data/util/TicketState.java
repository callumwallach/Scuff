package nz.co.scuff.data.util;

/**
 * Created by Callum on 29/04/2015.
 */
public enum TicketState {

    ISSUED(0), RECEIVED(1), STAMPED(2);

    private final int value;

    TicketState(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

}
