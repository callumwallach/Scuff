package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 17/03/2015.
 */
public class Passenger extends Person implements Parcelable {

    private School school;

    public Passenger() { }

    public Passenger(String name, School school, Gender gender, String pix) {
        super(name, gender, pix);
        this.school = school;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    protected Passenger(Parcel in) {
        super(in);
        school = (School) in.readValue(School.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(school);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Passenger> CREATOR = new Parcelable.Creator<Passenger>() {
        @Override
        public Passenger createFromParcel(Parcel in) {
            return new Passenger(in);
        }

        @Override
        public Passenger[] newArray(int size) {
            return new Passenger[size];
        }
    };
}