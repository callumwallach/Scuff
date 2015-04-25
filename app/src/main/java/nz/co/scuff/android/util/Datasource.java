package nz.co.scuff.android.util;

import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 18/03/2015.
 */
public class Datasource {
    private static Datasource ourInstance = new Datasource();

    public static Datasource getInstance() {
        return ourInstance;
    }

    private Datasource() {
    }

    public static School getSchool(Family family) {
        return null;
    }

}
