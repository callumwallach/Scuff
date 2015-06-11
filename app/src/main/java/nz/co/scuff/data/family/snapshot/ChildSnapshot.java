package nz.co.scuff.data.family.snapshot;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

import nz.co.scuff.data.base.snapshot.ModifiableSnapshot;
import nz.co.scuff.data.family.ChildData;
import nz.co.scuff.data.family.PersonalData;

/**
 * Created by Callum on 4/05/2015.
 */
public class ChildSnapshot extends ModifiableSnapshot {

    @Expose
    private long childId;
    @Expose
    private ChildData childData;
    @Expose
    private Set<Long> parentIds;
    @Expose
    private Set<Long> ticketIds;

    public ChildSnapshot() {
        super();
        parentIds = new HashSet<>();
        ticketIds = new HashSet<>();
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public ChildData getChildData() {
        return childData;
    }

    public void setChildData(ChildData childData) {
        this.childData = childData;
    }

    public Set<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(Set<Long> parentIds) {
        this.parentIds = parentIds;
    }

    public Set<Long> getTicketIds() {
        return ticketIds;
    }

    public void setTicketIds(Set<Long> ticketIds) {
        this.ticketIds = ticketIds;
    }

    @Override
    public String toString() {
        return "ChildSnapshot{" +
                "childId=" + childId +
                ", childData=" + childData +
                ", parentIds=" + parentIds +
                ", ticketIds=" + ticketIds +
                '}';
    }
}
