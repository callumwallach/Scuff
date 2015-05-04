package nz.co.scuff.data.school;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.Parent;

/**
 * Created by Callum on 17/03/2015.
 */
public class Bus implements Parcelable {

    private String name;
    private Parent driver;
    private HashSet<Child> children;

    public Bus(String name) {
        this.name = name;
    }

    public Parent getDriver() {
        return driver;
    }

    public void setDriver(Parent driver) {
        this.driver = driver;
    }

    public boolean addPassenger(Child child) {
        return this.children.add(child);
    }

    public boolean removePassenger(Child child) {
        return this.children.remove(child);
    }

    public HashSet<Child> getChildren() {
        return children;
    }

    public void setChildren(HashSet<Child> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    protected Bus(Parcel in) {
        name = in.readString();
        driver = (Parent) in.readValue(Parent.class.getClassLoader());
        children = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(driver);
        dest.writeValue(children);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bus> CREATOR = new Parcelable.Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };
}