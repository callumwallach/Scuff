package nz.co.scuff.android.util;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
//import com.activeandroid.app.Application;

import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 18/03/2015.
 */
public class ScuffApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
//        ActiveAndroid.dispose();
        ActiveAndroid.initialize(this);
        ActiveAndroid.setLoggingEnabled(true);
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

    public School getSchool() {
        return school;
    }

    public void setSchool(School s) {
        school = s;
    }

    private School school;
    private Family family;


}