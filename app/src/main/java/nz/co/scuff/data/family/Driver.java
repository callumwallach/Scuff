package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.HashSet;

import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 17/03/2015.
 */
public class Driver extends Person implements Parcelable {

    private String email;
    private String phone;
    private HashMap<School, Route> schoolRoutes;

    public Driver() { }

    public Driver(String name, Gender gender) {
        this(name, gender, null);
    }

    public Driver(String name, Gender gender, String pix) {
        super(name, gender, pix);
        schoolRoutes = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void addSchoolRoute(School school, Route route) {
        schoolRoutes.put(school, route);
    }

    public void removeSchoolRoute(School school, Route route) {
        schoolRoutes.remove(school);
    }

    public HashMap<School, Route> getSchoolRoutes() {
        return schoolRoutes;
    }

    public void setSchoolRoutes(HashMap<School, Route> schoolRoutes) {
        this.schoolRoutes = schoolRoutes;
    }

    public HashSet<Route> getRoutesForSchool(School school) {
        HashSet<Route> routes = new HashSet<>();
        routes.add(this.schoolRoutes.get(school));
        return routes;
    }

    protected Driver(Parcel in) {
        super(in);
        email = in.readString();
        phone = in.readString();
        schoolRoutes = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeValue(schoolRoutes);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Driver> CREATOR = new Parcelable.Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };
}