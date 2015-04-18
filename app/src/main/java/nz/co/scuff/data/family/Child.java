package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 17/03/2015.
 */
public class Child extends Person implements Parcelable {

    private School school;

    public Child(String name) {
        super(name);
    }

    public Child(String name, Gender gender) {
        super(name, gender);
    }

    public Child(String name, Gender gender, String pix) {
        super(name, gender, pix);
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    protected Child(Parcel in) {
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