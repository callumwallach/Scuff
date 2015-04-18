package nz.co.scuff.util;

import android.app.Application;
import android.content.Context;

import nz.co.scuff.data.family.Family;

/**
 * Created by Callum on 18/03/2015.
 */
public class ScuffContextProvider extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family f) {
        family = f;
    }

    private Family family;


}
