package nz.co.scuff.data.relationship;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="PassengerSchools")
public class PassengerSchool extends Model implements Serializable {

    @Column(name = "PassengerFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Passenger passenger;
    @Column(name = "SchoolFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private School school;

    public PassengerSchool() {
        super();
    }

    public PassengerSchool(Passenger passenger, School school) {
        this.passenger = passenger;
        this.school = school;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}