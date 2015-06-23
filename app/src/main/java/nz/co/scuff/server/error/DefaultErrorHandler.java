package nz.co.scuff.server.error;

import android.util.Log;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.event.ErrorEvent;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by Callum on 15/05/2015.
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final String TAG = "DefaultErrorHandler";
    private static final boolean D = true;

    @Override
    public Throwable handleError(RetrofitError cause) {
        if (D) Log.d(TAG, "handleError cause=" + cause);

        /*Response r = cause.getResponse();
        if (r != null) {
            ErrorResponse body = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
            if (D) Log.d(TAG, "body="+body);
            switch (body.getErrorCode()) {
                case JOURNEY_NOT_FOUND:
                case PERSON_NOT_FOUND:
                case SCHOOL_NOT_FOUND:
                case ROUTE_NOT_FOUND:
                    ResourceException e = new ResourceException(body.getMessage(),
                            cause, body.getReason(), body.getErrorCode());
                    ErrorEvent event = new ErrorEvent(e);
                    if (D) Log.d(TAG, "posting event="+event);
                    EventBus.getDefault().post(event);
                    return e;
                default:
            }
        }
        return cause;*/

        if (cause.getResponse() != null && cause.getResponse().getStatus() == 404) {
            ErrorResponse errorResponse = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
            ResourceException exception =
                    new ResourceException(errorResponse.getMessage(), errorResponse.getReason(), errorResponse.getErrorCode());
            ErrorEvent event = new ErrorEvent(exception);
            if (D) Log.d(TAG, "posting event=" + event);
            EventBus.getDefault().post(event);
            return exception;
        } else if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            NetworkException exception =
                    new NetworkException("Network error", "There was a problem connecting to the Scuff server", ErrorContextCode.NETWORK_ERROR);
            ErrorEvent event = new ErrorEvent(exception);
            if (D) Log.d(TAG, "posting event=" + event);
            EventBus.getDefault().post(event);
            return exception;
        }
        return cause;


/*        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            errorDescription = "Network error";
        } else {
            if (cause.getResponse() == null) {
                errorDescription = "Unknown error";
            } else {
                // Error message handling - return a simple error to Retrofit handlers..
                ErrorResponse errorResponse = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
                switch (errorResponse.getErrorCode()) {
                    case JOURNEY_NOT_FOUND:
                    case PERSON_NOT_FOUND:
                    case SCHOOL_NOT_FOUND:
                    case ROUTE_NOT_FOUND:
                        return new ResourceException();
                }
                errorDescription = errorResponse.getMessage();
            }
        }

        return new ScuffException(errorDescription);*/
    }

}
