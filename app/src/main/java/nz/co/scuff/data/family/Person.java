package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import nz.co.scuff.android.util.Constants;

/**
 * Created by Callum on 17/03/2015.
 */
public abstract class Person implements Parcelable {

    public enum Gender {
        MALE, FEMALE
    }

    private String name;
    private Gender gender;
    private String pix;

    public Person() {}

    public Person(String name, Gender gender) {
        this(name, gender, null);
    }

    public Person(String name, Gender gender, String pix) {
        this.name = name;
        this.gender = gender;
        this.pix = pix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPix() {
        return pix;
    }

    public void setPix(String pix) {
        this.pix = pix;
    }

    @Override
    public String toString() {
        return name;
    }

    protected Person(Parcel in) {
        name = in.readString();
        pix = in.readString();
        gender = (Gender) in.readValue(Gender.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pix);
        dest.writeValue(gender);
    }

}
