package nz.co.scuff.android.data;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Locale;

public class LoopJHttpClient {

    private static final String TAG = "LoopJHttpClient";
    private static final boolean D = true;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams requestParams, AsyncHttpResponseHandler responseHandler) {
        client.post(url, requestParams, responseHandler);
    }

    public static void debugLoopJ(String methodName,String url, RequestParams requestParams, byte[] response, Header[] headers, int statusCode, Throwable t) {

        if (D) {
            Log.d(TAG, client.getUrlWithQueryString(false, url, requestParams));

            if (headers != null) {
                Log.e(TAG, methodName);
                Log.d(TAG, "Return Headers:");
                for (Header h : headers) {
                    String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
                    Log.d(TAG, _h);
                }
            }

            if (t != null) {
                Log.d(TAG, "Throwable:" + t);
            }

            Log.e(TAG, "StatusCode: " + statusCode);

            if (response != null) {
                Log.d(TAG, "Response: " + new String(response));
            }

        }
    }

}