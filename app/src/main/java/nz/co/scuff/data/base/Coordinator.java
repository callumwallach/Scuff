package nz.co.scuff.data.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import nz.co.scuff.data.base.snapshot.CoordinatorSnapshot;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.PersonalData;
import nz.co.scuff.data.family.snapshot.AdultSnapshot;
import nz.co.scuff.data.institution.InstitutionData;
import nz.co.scuff.data.institution.Route;
import nz.co.scuff.data.institution.snapshot.InstitutionSnapshot;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.place.Place;
import nz.co.scuff.data.relationship.FriendRelationship;
import nz.co.scuff.data.relationship.GuidingRelationship;
import nz.co.scuff.data.relationship.JourneyRelationship;
import nz.co.scuff.data.relationship.ParentalRelationship;
import nz.co.scuff.data.relationship.PlaceRelationship;
import nz.co.scuff.data.util.TrackingState;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Callum on 3/06/2015.
 */
@Table(name="Coordinators")
public class Coordinator extends ModifiableEntity implements Snapshotable, Comparable {

    private static final String TAG = "Coordinator";
    private static final boolean D = true;

    public enum CoordinatorType {
        ADULT(0), INSTITUTION(1);

        private final int value;

        private CoordinatorType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    @Column(name="CoordinatorId")
    protected long coordinatorId;

    @Column(name="LastLogin")
    private Timestamp lastRefresh;

    // one to many
    private SortedSet<Route> routes;

    // many to many
    private Set<PlaceRelationship> places;
    private Set<FriendRelationship> friends;
    private Set<JourneyRelationship> pastJourneys;
    private Set<JourneyRelationship> currentJourneys;

    @Column(name="Type")
    protected CoordinatorType type;

    // institutions
    @Column(name="InstitutionDataFK")
    private InstitutionData institutionData;

    // one to many
    private Set<GuidingRelationship> guides;

    // adults
    @Column(name="PersonalDataFK")
    private PersonalData personalData;

    // many to many
    private Set<ParentalRelationship> children;
    private Set<GuidingRelationship> guidees;

    public Coordinator() {
        super();
    }

    public long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public Timestamp getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(Timestamp lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public SortedSet<Route> getRoutes() {
        if (this.routes == null) {
            this.routes = new TreeSet<>(getMany(Route.class, "OwnerFK"));
        }
        return routes;
    }

    public void setRoutes(SortedSet<Route> routes) {
        this.routes = routes;
    }

    public SortedSet<Place> getPlaces() {
        List<Place> places = new Select()
                .from(Place.class)
                .innerJoin(PlaceRelationship.class).on("Places.Id = PlaceRelationships.PlaceFK")
                .where("PlaceRelationships.CoordinatorFK = ?", getId())
                .execute();
        return new TreeSet<>(places);
    }

    public Set<PlaceRelationship> getPlaceRelationships() {
        if (this.places == null) {
            List<PlaceRelationship> list = new Select()
                    .from(PlaceRelationship.class)
                    .where("CoordinatorFK = ?", getId())
                    .execute();
            this.places = new HashSet<>(list);
        }
        return this.places;
    }

    public void setPlaces(Set<PlaceRelationship> places) {
        this.places = places;
    }

    public SortedSet<Coordinator> getFriends() {
        List<Coordinator> friends = new Select()
                .from(Coordinator.class)
                .innerJoin(FriendRelationship.class).on("Coordinators.Id = FriendRelationships.Coordinator1FK")
                .where("FriendRelationships.Coordinator2FK = ?", getId())
                .execute();
        if (friends == null || friends.isEmpty()) {
            friends = new Select()
                    .from(Coordinator.class)
                    .innerJoin(FriendRelationship.class).on("Coordinators.Id = FriendRelationships.Coordinator2FK")
                    .where("FriendRelationships.Coordinator1FK = ?", getId())
                    .execute();
        }
        return new TreeSet<>(friends);
    }

     public Set<FriendRelationship> getFriendRelationships() {
        if (friends == null) {
            List<FriendRelationship> list = new Select()
                    .from(FriendRelationship.class)
                    .where("Coordinator1FK = ?", getId())
                    .or("Coordinator2FK = ?", getId())
                    .execute();
            friends = new HashSet<>(list);
        }
        return friends;
    }

    public void setFriends(Set<FriendRelationship> friends) {
        this.friends = friends;
    }

    public SortedSet<Journey> getPastJourneys() {
        List<Journey> journeys = new Select()
                .from(Journey.class)
                .innerJoin(JourneyRelationship.class).on("Journeys.Id = JourneyRelationships.JourneyFK")
                .where("JourneyRelationships.CoordinatorFK = ?", getId())
                .and("Journeys.State = ?", TrackingState.COMPLETED)
                .execute();
        return new TreeSet<>(journeys);
    }

    public Set<JourneyRelationship> getPastJourneyRelationships() {
        if (pastJourneys == null) {
            List<JourneyRelationship> list = new Select()
                    .from(JourneyRelationship.class)
                    .where("CoordinatorFK = ?", getId())
                    .execute();
            pastJourneys = new HashSet<>(list);
        }
        return pastJourneys;
    }

    public void setPastJourneys(Set<JourneyRelationship> pastJourneys) {
        this.pastJourneys = pastJourneys;
    }

    public SortedSet<Journey> getCurrentJourneys() {
        List<Journey> journeys = new Select()
                .from(Journey.class)
                .innerJoin(JourneyRelationship.class).on("Journeys.Id = JourneyRelationships.JourneyFK")
                .where("JourneyRelationships.CoordinatorFK = ?", getId())
                .and("Journeys.State != ?", TrackingState.COMPLETED)
                .execute();
        return new TreeSet<>(journeys);
    }

    public Set<JourneyRelationship> getCurrentJourneyRelationships() {
        if (currentJourneys == null) {
            List<JourneyRelationship> list = new Select()
                    .from(JourneyRelationship.class)
                    .where("CoordinatorFK = ?", getId())
                    .execute();
            currentJourneys = new HashSet<>(list);
        }
        return currentJourneys;
    }

    public void setCurrentJourneys(Set<JourneyRelationship> currentJourneys) {
        this.currentJourneys = currentJourneys;
    }

    public CoordinatorType getType() {
        return type;
    }

    public void setType(CoordinatorType type) {
        this.type = type;
    }

    // institutions
    public InstitutionData getInstitutionData() {
        if (institutionData == null) {
            institutionData = InstitutionData.load(InstitutionData.class, coordinatorId);
        }
        return institutionData;
    }

    public void setInstitutionData(InstitutionData institutionData) {
        this.institutionData = institutionData;
    }

    public SortedSet<Coordinator> getGuides() {
        List<Coordinator> guides = new Select()
                .from(Coordinator.class)
                .innerJoin(GuidingRelationship.class).on("Coordinators.Id = GuidingRelationships.AdultFK")
                .where("GuidingRelationships.InstitutionFK = ?", getId())
                .execute();
        return new TreeSet<>(guides);
    }

    public Set<GuidingRelationship> getGuideRelationships() {
        if (this.guides == null) {
            List<GuidingRelationship> list = new Select()
                    .from(GuidingRelationship.class)
                    .where("InstitutionFK = ?", getId())
                    .execute();
            guides = new HashSet<>(list);
        }
        return guides;
    }

    public void setGuides(Set<GuidingRelationship> guides) {
        this.guides = guides;
    }

    // adults
    public PersonalData getPersonalData() {
        if (personalData == null) {
            personalData = PersonalData.load(PersonalData.class, coordinatorId);
        }
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }

    public SortedSet<Child> getChildren() {
        return new TreeSet<>(getManyThrough(Child.class, ParentalRelationship.class, "ChildFK", "AdultFK"));
    }

    public void setChildren(Set<ParentalRelationship> children) {
        this.children = children;
    }

    public Set<ParentalRelationship> getChildrenRelationships() {
        if (this.children == null) {
            List<ParentalRelationship> list = new Select()
                    .from(ParentalRelationship.class)
                    .where("AdultFK = ?", getId())
                    .execute();
            children = new HashSet<>(list);
        }
        return children;
    }

    public SortedSet<Coordinator> getGuidees() {
        List<Coordinator> institutions = new Select()
                .from(Coordinator.class)
                .innerJoin(GuidingRelationship.class).on("Coordinators.Id = GuidingRelationships.InstitutionFK")
                .where("GuidingRelationships.AdultFK = ?", getId())
                .execute();
        return new TreeSet<>(institutions);
    }

    public void setGuidees(Set<GuidingRelationship> guidees) {
        this.guidees = guidees;
    }

    public Set<GuidingRelationship> getGuideeRelationships() {
        if (this.guidees == null) {
            List<GuidingRelationship> list = new Select()
                    .from(GuidingRelationship.class)
                    .where("AdultFK = ?", getId())
                    .execute();
            guidees = new HashSet<>(list);
        }
        return guidees;
    }

    // helper
    public String getName() {
        String name;
        switch (type) {
            case ADULT:
                name = getPersonalData().getFirstName();
            break;
            case INSTITUTION:
            default:
                name = getInstitutionData().getName();
        }
        return name;
    }

    public String getPicture() {
        String picture;
        switch (type) {
            case ADULT:
                picture = getPersonalData().getPicture();
                break;
            case INSTITUTION:
            default:
                picture = null;
        }
        return picture;
    }

    public void refresh(CoordinatorSnapshot snapshot) {
        this.type = (snapshot instanceof AdultSnapshot) ? CoordinatorType.ADULT : CoordinatorType.INSTITUTION;
        this.coordinatorId = snapshot.getCoordinatorId();
        this.lastRefresh = snapshot.getLastRefresh();
        this.active = snapshot.isActive();
        this.lastModified = snapshot.getLastModified();
    }

    public CoordinatorSnapshot toSnapshot() {
        CoordinatorSnapshot toReturn;
        if (this.type == CoordinatorType.ADULT) {
            AdultSnapshot snapshot = new AdultSnapshot();
            snapshot.setPersonalData(this.personalData);
            toReturn = snapshot;
        } else /* this.type == INSTITUTION */ {
            InstitutionSnapshot snapshot = new InstitutionSnapshot();
            snapshot.setInstitutionData(this.institutionData);
            toReturn = snapshot;
        }
        toReturn.setCoordinatorId(coordinatorId);
        toReturn.setLastRefresh(lastRefresh);
        toReturn.setActive(active);
        toReturn.setLastModified(lastModified);
        return toReturn;
    }

    public static Coordinator findById(long pk) {
        return new Select()
                .from(Coordinator.class)
                .where("CoordinatorId = ?", pk)
                .executeSingle();
    }

    public static Coordinator findByEmail(String email) {
        Coordinator toReturn = new Select()
                .from(Coordinator.class)
                .innerJoin(PersonalData.class).on("Coordinators.PersonalDataFK = PersonalDatas.Id")
                .where("PersonalDatas.Email = ?", email)
                .executeSingle();
        if (toReturn == null) {
            // search on institution instead
            toReturn = new Select()
                    .from(Coordinator.class)
                    .innerJoin(InstitutionData.class).on("Coordinators.InstitutionDataFK = InstitutionDatas.Id")
                    .where("InstitutionDatas.Email = ?", email)
                    .executeSingle();
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinator c = (Coordinator) o;

        return coordinatorId == c.coordinatorId;

    }

    @Override
    public int hashCode() {
        return (int) (coordinatorId ^ (coordinatorId >>> 32));
    }

    @Override
    public String toString() {
        return "Coordinator{" +
                "coordinatorId=" + coordinatorId +
                ", lastRefresh=" + lastRefresh +
                ", routes=" + routes +
                ", places=" + places +
                ", friends=" + friends +
                ", pastJourneys=" + pastJourneys +
                ", currentJourneys=" + currentJourneys +
                ", type=" + type +
                ", institutionData=" + institutionData +
                ", guides=" + guides +
                ", personalData=" + personalData +
                ", children=" + children +
                ", guidees=" + guidees +
                "} " + super.toString();
    }

    @Override
    public int compareTo(Object another) {
        Coordinator that = (Coordinator)another;
        if (this.equals(that)) return 0;
        if (type == CoordinatorType.ADULT) {
            return this.personalData.compareTo(that.personalData);
        } else {
            return this.institutionData.compareTo(that.institutionData);
        }
    }

    protected Coordinator(Parcel in) {
        super(in);
        coordinatorId = in.readLong();
        lastRefresh = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
/*        routes = (SortedSet) in.readValue(SortedSet.class.getClassLoader());
        if (in.readByte() == 0x01) {
            places = new ArrayList<PlaceRelationship>();
            in.readList(places, PlaceRelationship.class.getClassLoader());
        } else {
            places = null;
        }
        if (in.readByte() == 0x01) {
            friends = new ArrayList<FriendRelationship>();
            in.readList(friends, FriendRelationship.class.getClassLoader());
        } else {
            friends = null;
        }
        if (in.readByte() == 0x01) {
            pastJourneys = new ArrayList<JourneyRelationship>();
            in.readList(pastJourneys, JourneyRelationship.class.getClassLoader());
        } else {
            pastJourneys = null;
        }
        if (in.readByte() == 0x01) {
            currentJourneys = new ArrayList<JourneyRelationship>();
            in.readList(currentJourneys, JourneyRelationship.class.getClassLoader());
        } else {
            currentJourneys = null;
        }*/
        type = (CoordinatorType) in.readValue(CoordinatorType.class.getClassLoader());
        institutionData = (InstitutionData) in.readValue(InstitutionData.class.getClassLoader());
/*        if (in.readByte() == 0x01) {
            guides = new ArrayList<GuidingRelationship>();
            in.readList(guides, GuidingRelationship.class.getClassLoader());
        } else {
            guides = null;
        }*/
        personalData = (PersonalData) in.readValue(PersonalData.class.getClassLoader());
/*        if (in.readByte() == 0x01) {
            children = new ArrayList<ParentalRelationship>();
            in.readList(children, ParentalRelationship.class.getClassLoader());
        } else {
            children = null;
        }
        if (in.readByte() == 0x01) {
            guidees = new ArrayList<GuidingRelationship>();
            in.readList(guidees, GuidingRelationship.class.getClassLoader());
        } else {
            guidees = null;
        }*/
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(coordinatorId);
        dest.writeValue(lastRefresh);
/*        dest.writeValue(routes);
        if (places == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(places);
        }
        if (friends == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(friends);
        }
        if (pastJourneys == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(pastJourneys);
        }
        if (currentJourneys == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(currentJourneys);
        }*/
        dest.writeValue(type);
        dest.writeValue(institutionData);
/*        if (guides == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(guides);
        }*/
        dest.writeValue(personalData);
/*        if (children == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(children);
        }
        if (guidees == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(guidees);
        }*/
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Coordinator> CREATOR = new Parcelable.Creator<Coordinator>() {
        @Override
        public Coordinator createFromParcel(Parcel in) {
            return new Coordinator(in);
        }

        @Override
        public Coordinator[] newArray(int size) {
            return new Coordinator[size];
        }
    };

}

