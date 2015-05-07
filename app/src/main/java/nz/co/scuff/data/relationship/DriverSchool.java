package nz.co.scuff.data.relationship;

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
public class DriverSchool extends Model implements Serializable {

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
}