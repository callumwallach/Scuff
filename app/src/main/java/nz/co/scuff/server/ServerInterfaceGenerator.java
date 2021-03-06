package nz.co.scuff.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;


import nz.co.scuff.android.util.Constants;
import nz.co.scuff.server.error.DefaultErrorHandler;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Callum on 26/04/2015.
 */
public class ServerInterfaceGenerator {

    // No need to instantiate this class.
    private ServerInterfaceGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {

        return createService(serviceClass, baseUrl, new DefaultErrorHandler());
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl, ErrorHandler errorHandler) {

        Gson gson = new GsonBuilder()
                .setDateFormat(Constants.JSON_DATE_FORMAT)
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL) // testing
                .setEndpoint(baseUrl)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(new OkHttpClient()))
                .setErrorHandler(errorHandler);

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }
}
