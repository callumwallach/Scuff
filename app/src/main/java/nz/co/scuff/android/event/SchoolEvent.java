package nz.co.scuff.android.event;

import java.util.List;

import nz.co.scuff.data.base.Coordinator;

/**
 * Created by Callum on 30/04/2015.
 */
public class SchoolEvent {

    private List<Coordinator> institutions;

    public SchoolEvent(List<Coordinator> institutions) {
        this.institutions = institutions;
    }

    public List<Coordinator> getInstitutions() {
        return institutions;
    }

    @Override
    public String toString() {
        String toReturn = "SchoolEvent{" +
                "institutions=[";
        for (Coordinator institution : institutions) {
            toReturn += institution;
            toReturn += " ";
        }
        toReturn += "]}";
        return toReturn;
    }
}
