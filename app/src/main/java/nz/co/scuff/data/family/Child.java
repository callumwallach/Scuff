package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.base.ModifiableEntity;
import nz.co.scuff.data.family.snapshot.ChildSnapshot;
import nz.co.scuff.data.journey.Ticket;
import nz.co.scuff.data.relationship.ParentalRelationship;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Children")
public class Child extends ModifiableEntity {

    @Column(name="ChildId")
    protected long childId;
    @Column(name="ChildDataFK")
    private ChildData childData;

    // many to many
    private List<ParentalRelationship> parents;
    // one to many
    private SortedSet<Ticket> tickets;

    public Child() { }

    public Child(ChildData data) {
        super();
        this.childData = data;
        this.parents = new ArrayList<>();
        this.tickets = new TreeSet<>();
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

    public SortedSet<Coordinator> getParents() {
        List<Coordinator> parents = new Select()
                .from(Coordinator.class)
                .innerJoin(ParentalRelationship.class).on("Coordinators.Id = ParentalRelationships.AdultFK")
                .where("ParentalRelationships.ChildFK = ?", getId())
                .execute();
        return new TreeSet<>(parents);
    }

    public List<ParentalRelationship> getParentRelationships() {
        if (this.parents == null) {
            this.parents = new ArrayList<>();
        }
        return parents;
    }

    public void setParents(List<ParentalRelationship> parents) {
        this.parents = parents;
    }

    public SortedSet<Ticket> getTickets() {
        return new TreeSet<>(getMany(Ticket.class, "ChildFK"));
    }

    public void setTickets(SortedSet<Ticket> tickets) {
        this.tickets = tickets;
    }

    public static Child findById(long pk) {
        return new Select()
                .from(Child.class)
                .where("ChildId = ?", pk)
                .executeSingle();
    }

    public void refresh(ChildSnapshot snapshot) {
        this.childId = snapshot.getChildId();
        this.childData = snapshot.getChildData();
        this.active = snapshot.isActive();
        this.lastModified = snapshot.getLastModified();
    }

    public ChildSnapshot toSnapshot() {
        ChildSnapshot snapshot = new ChildSnapshot();
        snapshot.setChildId(childId);
        snapshot.setChildData(childData);
        snapshot.setActive(active);
        snapshot.setLastModified(lastModified);
        return snapshot;
    }

    @Override
    public String toString() {
        return "Child{" +
                "childId=" + childId +
                ", childData=" + childData +
                ", parents=" + parents +
                ", tickets=" + tickets +
                "} " + super.toString();
    }

    protected Child(Parcel in) {
        super(in);
        childId = in.readLong();
        childData = (ChildData) in.readValue(ChildData.class.getClassLoader());
        if (in.readByte() == 0x01) {
            parents = new ArrayList<ParentalRelationship>();
            in.readList(parents, ParentalRelationship.class.getClassLoader());
        } else {
            parents = null;
        }
        tickets = (SortedSet) in.readValue(SortedSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(childId);
        dest.writeValue(childData);
        if (parents == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(parents);
        }
        dest.writeValue(tickets);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Child> CREATOR = new Parcelable.Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };
}