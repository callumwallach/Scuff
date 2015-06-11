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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Callum on 3/06/2015.
 */
@Table(name="Coordinators")
public class Coordinator extends ModifiableEntity implements Snapshotable, Comparable {

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

    // one to many
    private SortedSet<Route> routes;

    // many to many
    private List<PlaceRelationship> places;
    private List<FriendRelationship> friends;
    private List<JourneyRelationship> pastJourneys;
    private List<JourneyRelationship> currentJourneys;

    @Column(name="Type")
    protected CoordinatorType type;

    // institutions
    @Column(name="InstitutionDataFK")
    private InstitutionData institutionData;
    /*@Column(name="Name")
    private String name;*/

    // one to many
    private List<GuidingRelationship> guides;

    // adults
    @Column(name="PersonalDataFK")
    private PersonalData personalData;
    /*@Column(name="FirstName")
    private String firstName;
    @Column(name="MiddleName")
    private String middleName;
    @Column(name="LastName")
    private String lastName;
    @Column(name="Gender")
    private PersonalData.Gender gender;
    @Column(name="Picture")
    private String picture;
    @Column(name="Email")
    private String email;
    @Column(name="Phone")
    private String phone;*/

    // many to many
    private List<ParentalRelationship> children;
    private List<GuidingRelationship> guidees;

    public Coordinator() {
        super();
        this.routes = new TreeSet<>();
        this.places = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.pastJourneys = new ArrayList<>();
        this.currentJourneys = new ArrayList<>();
        // institutions
        this.guides = new ArrayList<>();
        // adults
        this.children = new ArrayList<>();
        this.guidees = new ArrayList<>();
    }

    public long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public SortedSet<Route> getRoutes() {
        return new TreeSet<>(getMany(Route.class, "OwnerFK"));
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

    public List<PlaceRelationship> getPlaceRelationships() {
        if (places == null) {
            places = new ArrayList<>();
        }
        return places;
    }

    public void setPlaces(List<PlaceRelationship> places) {
        this.places = places;
    }

    public SortedSet<Coordinator> getFriends() {
/*        String sql = new Select()
                .from(Coordinator.class)
                .innerJoin(FriendRelationship.class).on("Coordinators.Id = FriendRelationships.Coordinator1FK")
                .where("FriendRelationships.Coordinator2FK = ?", getId()).toSql();*/
        List<Coordinator> friends = new Select()
                .from(Coordinator.class)
                .innerJoin(FriendRelationship.class).on("Coordinators.Id = FriendRelationships.Coordinator1FK")
                .where("FriendRelationships.Coordinator2FK = ?", getId())
                .execute();
        if (friends == null || friends.isEmpty()) {
/*            sql = new Select()
                    .from(Coordinator.class)
                    .innerJoin(FriendRelationship.class).on("Coordinators.Id = FriendRelationships.Coordinator2FK")
                    .where("FriendRelationships.Coordinator1FK = ?", getId()).toSql();*/
            friends = new Select()
                    .from(Coordinator.class)
                    .innerJoin(FriendRelationship.class).on("Coordinators.Id = FriendRelationships.Coordinator2FK")
                    .where("FriendRelationships.Coordinator1FK = ?", getId())
                    .execute();
        }
        return new TreeSet<>(friends);
    }

     public List<FriendRelationship> getFriendRelationships() {
        if (friends == null) {
            friends = new ArrayList<>();
        }
        return friends;
    }

    public void setFriends(List<FriendRelationship> friends) {
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

    public List<JourneyRelationship> getPastJourneyRelationships() {
        if (pastJourneys == null) {
            pastJourneys = new ArrayList<>();
        }
        return pastJourneys;
    }

    public void setPastJourneys(List<JourneyRelationship> pastJourneys) {
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

    public List<JourneyRelationship> getCurrentJourneyRelationships() {
        if (currentJourneys == null) {
            currentJourneys = new ArrayList<>();
        }
        return currentJourneys;
    }

    public void setCurrentJourneys(List<JourneyRelationship> currentJourneys) {
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

    public List<GuidingRelationship> getGuideRelationships() {
        if (this.guides == null) {
            this.guides = new ArrayList<>();
        }
        return guides;
    }

    public void setGuides(List<GuidingRelationship> guides) {
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

    public void setChildren(List<ParentalRelationship> children) {
        this.children = children;
    }

    public List<ParentalRelationship> getChildrenRelationships() {
        if (this.children == null) {
            this.children = new ArrayList<>();
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

    public void setGuidees(List<GuidingRelationship> guidees) {
        this.guidees = guidees;
    }

    public List<GuidingRelationship> getGuideeRelationships() {
        if (this.guidees == null) {
            this.guidees = new ArrayList<>();
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
        if (type == CoordinatorType.ADULT) {
            return this.personalData.compareTo(that.personalData);
        } else {
            return this.institutionData.compareTo(that.institutionData);
        }
    }

    protected Coordinator(Parcel in) {
        super(in);
        coordinatorId = in.readLong();
        routes = (SortedSet) in.readValue(SortedSet.class.getClassLoader());
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
        }
        type = (CoordinatorType) in.readValue(CoordinatorType.class.getClassLoader());
        institutionData = (InstitutionData) in.readValue(InstitutionData.class.getClassLoader());
        if (in.readByte() == 0x01) {
            guides = new ArrayList<GuidingRelationship>();
            in.readList(guides, GuidingRelationship.class.getClassLoader());
        } else {
            guides = null;
        }
        personalData = (PersonalData) in.readValue(PersonalData.class.getClassLoader());
        if (in.readByte() == 0x01) {
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
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(coordinatorId);
        dest.writeValue(routes);
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
        }
        dest.writeValue(type);
        dest.writeValue(institutionData);
        if (guides == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(guides);
        }
        dest.writeValue(personalData);
        if (children == null) {
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
        }
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

