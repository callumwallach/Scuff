package nz.co.scuff.android.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.activeandroid.app.Application;

import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.journey.Journey;
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
//        ActiveAndroid.initialize(this);
//        ActiveAndroid.setLoggingEnabled(true);
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    private School school;
    private Family family;
    private Driver driver;
    private Journey journey;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver d) {
        driver = d;
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

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public String getAppId() {
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }


}
