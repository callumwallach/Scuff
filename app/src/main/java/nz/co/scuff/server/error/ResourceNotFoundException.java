package nz.co.scuff.server.error;

/**
 * Created by Callum on 15/05/2015.
 */
public class ResourceNotFoundException extends ScuffException {

    public ResourceNotFoundException(String message, String reason, ErrorContextCode errorCode) {
        super(message, reason, errorCode);
    }

    public ResourceNotFoundException(String message, Throwable throwable, String reason, ErrorContextCode errorCode) {
        super(message, throwable, reason, errorCode);
    }

    public ResourceNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
