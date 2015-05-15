package nz.co.scuff.server.error;

/**
 * Created by Callum on 15/05/2015.
 */
public class ErrorResponse {

    private String message;
    private String reason;
    private ErrorContextCode errorCode;

    public ErrorResponse(String message, String reason, ErrorContextCode errorCode) {
        this.message = message;
        this.reason = reason;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        final StringBuffer sb = new StringBuffer("ErrorResponse{");
        sb.append("message='").append(message).append('\'');
        sb.append(", reason='").append(reason).append('\'');
        sb.append(", errorCode=").append(errorCode);
        sb.append('}');
        return sb.toString();
    }
}
