package nz.co.scuff.data.institution.snapshot;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

import nz.co.scuff.data.base.snapshot.CoordinatorSnapshot;
import nz.co.scuff.data.institution.InstitutionData;

/**
 * Created by Callum on 4/05/2015.
 */
public class InstitutionSnapshot extends CoordinatorSnapshot {

    @Expose
    private InstitutionData institutionData;
    @Expose
    private Set<Long> guideIds;

    public InstitutionSnapshot() {
        super();
        guideIds = new HashSet<>();
    }

    public InstitutionData getInstitutionData() {
        return institutionData;
    }

    public void setInstitutionData(InstitutionData institutionData) {
        this.institutionData = institutionData;
    }

    public Set<Long> getGuideIds() {
        return guideIds;
    }

    public void setGuideIds(Set<Long> guideIds) {
        this.guideIds = guideIds;
    }

    @Override
    public String toString() {
        return "InstitutionSnapshot{" +
                "institutionData=" + institutionData +
                ", guideIds=" + guideIds +
                "} " + super.toString();
    }
}
