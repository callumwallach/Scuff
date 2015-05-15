package nz.co.scuff.server.error;

import android.util.Log;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.event.ErrorEvent;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Callum on 15/05/2015.
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final String TAG = "DefaultErrorHandler";
    private static final boolean D = true;

    @Override public Throwable handleError(RetrofitError cause) {
        if (D) Log.d(TAG, "handleError cause="+cause);

        /*Response r = cause.getResponse();
        if (r != null) {
            ErrorResponse body = (ErrorResponse) cause.getBodyAs(ErrorResponse.class);
            if (D) Log.d(TAG, "body="+body);
            switch (body.getErrorCode()) {
                case JOURNEY_NOT_FOUND:
                case PERSON_NOT_FOUND:
                case SCHOOL_NOT_FOUND:
                case ROUTE_NOT_FOUND:
                    ResourceNotFoundException e = new ResourceNotFoundException(body.getMessage(),
                            cause, body.getReason(), body.getErrorCode());
                    ErrorEvent event = new ErrorEvent(e);
                    if (D) Log.d(TAG, "posting event="+event);
                    EventBus.getDefault().post(event);
                    return e;
                default:
            }
        }
        return cause;*/

        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 404) {
            ResourceNotFoundException exception = new ResourceNotFoundException(cause.getMessage(), cause);
            ErrorEvent event = new ErrorEvent(exception);
            if (D) Log.d(TAG, "posting event="+event);
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
                        return new ResourceNotFoundException();
                }
                errorDescription = errorResponse.getMessage();
            }
        }

        return new ScuffException(errorDescription);*/
    }

}
