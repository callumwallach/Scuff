package nz.co.scuff.android.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.activeandroid.app.Application;

import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.journey.Journey;

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

    private Coordinator institution;
    private Coordinator coordinator;
    private Journey journey;

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Coordinator d) {
        coordinator = d;
    }

    public Coordinator getInstitution() {
        return institution;
    }

    public void setInstitution(Coordinator s) {
        institution = s;
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
