package nz.co.scuff.data.relationship;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="DriverSchools")
public class DriverSchool extends Model implements Serializable, Parcelable {

    @Column(name = "DriverFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Driver driver;
    @Column(name = "SchoolFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private School school;

    public DriverSchool() {
        super();
    }

    public DriverSchool(Driver driver, School school) {
        this.driver = driver;
        this.school = school;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    protected DriverSchool(Parcel in) {
        driver = (Driver) in.readValue(Driver.class.getClassLoader());
        school = (School) in.readValue(School.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(driver);
        dest.writeValue(school);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DriverSchool> CREATOR = new Parcelable.Creator<DriverSchool>() {
        @Override
        public DriverSchool createFromParcel(Parcel in) {
            return new DriverSchool(in);
        }

        @Override
        public DriverSchool[] newArray(int size) {
            return new DriverSchool[size];
        }
    };
}