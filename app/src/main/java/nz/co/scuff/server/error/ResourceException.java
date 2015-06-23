package nz.co.scuff.server.error;

/**
 * Created by Callum on 15/05/2015.
 */
public class ResourceException extends ScuffException {

    public ResourceException(String message, String reason, ErrorContextCode errorCode) {
        super(message, reason, errorCode);
    }

    public ResourceException(String message, Throwable throwable, String reason, ErrorContextCode errorCode) {
        super(message, throwable, reason, errorCode);
    }

    public ResourceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
