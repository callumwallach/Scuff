package nz.co.scuff.android.util;

import java.util.List;

import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 30/04/2015.
 */
public class SchoolEvent {

    private List<School> schools;

    public SchoolEvent(List<School> schools) {
        this.schools = schools;
    }

    public List<School> getSchools() {
        return schools;
    }

    @Override
    public String toString() {
        String toReturn = "SchoolEvent{" +
                "schools=[";
        for (School school : schools) {
            toReturn += school;
            toReturn += " ";
        }
        toReturn += "]}";
        return toReturn;
    }
}
