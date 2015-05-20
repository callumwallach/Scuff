package nz.co.scuff.data.school;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.relationship.DriverSchool;
import nz.co.scuff.data.relationship.PassengerSchool;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Schools")
public class School extends Model implements Comparable, Serializable, Parcelable {

    @Column(name="SchoolId")
    private long schoolId;
    @Column(name="Name")
    private String name;
    @Column(name="Latitude")
    private double latitude;
    @Column(name="Longitude")
    private double longitude;
    @Column(name="Altitude")
    private double altitude;

    // one to many
    /*@Column(name="Routes")*/
    private SortedSet<Route> routes;
    /*@Column(name="Journeys")*/
    private SortedSet<Journey> journeys;

    // many to many
    private List<PassengerSchool> passengerSchools;
    private List<DriverSchool> driverSchools;

    public School() {
        super();
    }

    public School(String name, double latitude, double longitude, double altitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;

        this.routes = new TreeSet<>();
        this.journeys = new TreeSet<>();
        this.passengerSchools = new ArrayList<>();
        this.driverSchools = new ArrayList<>();

    }

    public School(SchoolSnapshot snapshot) {
        this(snapshot.getName(), snapshot.getLatitude(), snapshot.getLongitude(), snapshot.getAltitude());
        this.schoolId = snapshot.getSchoolId();
    }

    public long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    // TODO load from db?
    public SortedSet<Route> getRoutes() {
        SortedSet<Route> returnRoutes = new TreeSet<>();
        List<Route> foundRoutes = getMany(Route.class, "SchoolFK");
        if (foundRoutes != null) {
            returnRoutes.addAll(foundRoutes);
        }
        return returnRoutes;
        /*if (this.routes == null) {
            this.routes = new TreeSet<>();
        }
        return routes;*/
    }

    public void setRoutes(SortedSet<Route> routes) {
        this.routes = routes;
    }

    public SortedSet<Journey> getJourneys() {
        //List<Journey> journeys = getMany(Journey.class, "SchoolFK");
        if (this.journeys == null) {
            this.journeys = new TreeSet<>();
        }
        return journeys;

    }

    public void setJourneys(SortedSet<Journey> journeys) {
        this.journeys = journeys;
    }

    public SortedSet<Passenger> getPassengers() {
        List<Passenger> passengers = new Select()
                .from(Passenger.class)
                .innerJoin(PassengerSchool.class).on("Passengers.Id = PassengerSchools.PassengerFK")
                .where("PassengerSchools.SchoolFK = ?", getId())
                .execute();
        return new TreeSet<>(passengers);
    }

    public List<PassengerSchool> getPassengerSchools() {
        if (passengerSchools == null) {
            passengerSchools = new ArrayList<>();
        }
        return passengerSchools;
    }

    public void setPassengerSchools(List<PassengerSchool> passengerSchools) {
        this.passengerSchools = passengerSchools;
    }

    public List<DriverSchool> getDriverSchools() {
        if (driverSchools == null) {
            driverSchools = new ArrayList<>();
        }
        return driverSchools;
    }

    public void setDriverSchools(List<DriverSchool> driverSchools) {
        this.driverSchools = driverSchools;
    }

    public static School findBySchoolId(long schoolId) {
        return new Select()
                .from(School.class)
                .where("SchoolId = ?", schoolId)
                .executeSingle();
    }

    public SchoolSnapshot toSnapshot() {
        SchoolSnapshot snapshot = new SchoolSnapshot();
        snapshot.setSchoolId(schoolId);
        snapshot.setName(name);
        snapshot.setLatitude(latitude);
        snapshot.setLongitude(longitude);
        snapshot.setAltitude(altitude);
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        School school = (School) o;

        return schoolId == school.schoolId;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (schoolId ^ (schoolId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("School{");
        sb.append("schoolId=").append(schoolId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", altitude=").append(altitude);
        sb.append(", routes=").append(routes);
        sb.append(", journeys=").append(journeys);
        sb.append(", passengerSchools=").append(passengerSchools);
        sb.append(", driverSchools=").append(driverSchools);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Object another) {
        School other = (School)another;
        return this.name.compareTo(other.name);
    }

    protected School(Parcel in) {
        schoolId = in.readLong();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        routes = (SortedSet) in.readValue(SortedSet.class.getClassLoader());
        journeys = (SortedSet) in.readValue(SortedSet.class.getClassLoader());
        if (in.readByte() == 0x01) {
            passengerSchools = new ArrayList<PassengerSchool>();
            in.readList(passengerSchools, PassengerSchool.class.getClassLoader());
        } else {
            passengerSchools = null;
        }
        if (in.readByte() == 0x01) {
            driverSchools = new ArrayList<DriverSchool>();
            in.readList(driverSchools, DriverSchool.class.getClassLoader());
        } else {
            driverSchools = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(schoolId);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeValue(routes);
        dest.writeValue(journeys);
        if (passengerSchools == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(passengerSchools);
        }
        if (driverSchools == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(driverSchools);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<School> CREATOR = new Parcelable.Creator<School>() {
        @Override
        public School createFromParcel(Parcel in) {
            return new School(in);
        }

        @Override
        public School[] newArray(int size) {
            return new School[size];
        }
    };
}