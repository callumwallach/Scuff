package nz.co.scuff.server.error;

/**
 * Created by Callum on 15/05/2015.
 */
public class ScuffException extends Exception {

    private String reason;
    private ErrorContextCode errorCode;

    public ScuffException(String message, String reason, ErrorContextCode errorCode) {
        super(message);
        this.reason = reason;
        this.errorCode = errorCode;
    }

    public ScuffException(String message, Throwable throwable, String reason, ErrorContextCode errorCode) {
        super(message, throwable);
        this.reason = reason;
        this.errorCode = errorCode;
    }

    public ScuffException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ErrorContextCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorContextCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ScuffException{");
        sb.append("reason='").append(reason).append('\'');
        sb.append(", errorCode=").append(errorCode);
        sb.append('}');
        return sb.toString();
    }
}
