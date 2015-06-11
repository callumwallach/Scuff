package nz.co.scuff.data.family.snapshot;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

import nz.co.scuff.data.base.snapshot.CoordinatorSnapshot;
import nz.co.scuff.data.family.PersonalData;

/**
 * Created by Callum on 4/05/2015.
 */
public class AdultSnapshot extends CoordinatorSnapshot {

    @Expose
    private PersonalData personalData;
    @Expose
    private Set<Long> childrenIds;
    @Expose
    private Set<Long> guideeIds;

    public AdultSnapshot() {
        super();
        childrenIds = new HashSet<>();
        guideeIds = new HashSet<>();
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }

    public Set<Long> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(Set<Long> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public Set<Long> getGuideeIds() {
        return guideeIds;
    }

    public void setGuideeIds(Set<Long> guideeIds) {
        this.guideeIds = guideeIds;
    }

    @Override
    public String toString() {
        return "AdultSnapshot{" +
                "personalData=" + personalData +
                ", childrenIds=" + childrenIds +
                ", guideeIds=" + guideeIds +
                "} " + super.toString();
    }
}
