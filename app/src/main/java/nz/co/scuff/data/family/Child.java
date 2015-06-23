package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.base.ModifiableEntity;
import nz.co.scuff.data.base.Snapshotable;
import nz.co.scuff.data.family.snapshot.ChildSnapshot;
import nz.co.scuff.data.journey.Ticket;
import nz.co.scuff.data.relationship.ParentalRelationship;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Children")
public class Child extends ModifiableEntity implements Snapshotable, Comparable {

    @Column(name="ChildId")
    protected long childId;
    @Column(name="ChildDataFK")
    private ChildData childData;

    // many to many
    private Set<ParentalRelationship> parents;
    // one to many
    private SortedSet<Ticket> tickets;

    public Child() { }

    public Child(ChildData data) {
        super();
        this.childData = data;
        /*this.parents = new ArrayList<>();
        this.tickets = new TreeSet<>();*/
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

    public Set<ParentalRelationship> getParentRelationships() {
        if (this.parents == null) {
            List<ParentalRelationship> list = new Select()
                    .from(ParentalRelationship.class)
                    .where("ChildFK = ?", getId())
                    .execute();
            parents = new HashSet<>(list);
        }
        return parents;
    }

    public void setParents(Set<ParentalRelationship> parents) {
        this.parents = parents;
    }

    public SortedSet<Ticket> getTickets() {
        if (this.tickets == null) {
            this.tickets = new TreeSet<>(getMany(Ticket.class, "ChildFK"));
        }
        return tickets;
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
        //this.childData = snapshot.getChildData();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Child child = (Child) o;

        return childId == child.childId;

    }

    @Override
    public int hashCode() {
        int result = 31 * (int) (childId ^ (childId >>> 32));
        return result;
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

    @Override
    public int compareTo(Object another) {
        Child that = (Child)another;
        if (this.equals(that)) return 0;
        return this.childData.compareTo(that.childData);
    }

    protected Child(Parcel in) {
        super(in);
        childId = in.readLong();
        childData = (ChildData) in.readValue(ChildData.class.getClassLoader());
/*        if (in.readByte() == 0x01) {
            parents = new ArrayList<ParentalRelationship>();
            in.readList(parents, ParentalRelationship.class.getClassLoader());
        } else {
            parents = null;
        }
        tickets = (SortedSet) in.readValue(SortedSet.class.getClassLoader());*/
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
/*        if (parents == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(parents);
        }
        dest.writeValue(tickets);*/
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