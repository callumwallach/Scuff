package nz.co.scuff.android.event;

import nz.co.scuff.server.error.ScuffException;

/**
 * Created by Callum on 30/04/2015.
 */
public class ErrorEvent {

    private ScuffException error;

    public ErrorEvent(ScuffException error) {
        this.error = error;
    }

    public ScuffException getError() {
        return error;
    }

    public void setError(ScuffException error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ErrorEvent{");
        sb.append("error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
