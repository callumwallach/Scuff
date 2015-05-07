package nz.co.scuff.data.relationship;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.family.Passenger;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="DriverPassengers")
public class DriverPassenger extends Model implements Serializable {

    @Column(name = "DriverFK", onDelete= Column.ForeignKeyAction.CASCADE)
    public Driver driver;
    @Column(name = "PassengerFK", onDelete= Column.ForeignKeyAction.CASCADE)
    public Passenger passenger;

    public DriverPassenger() {
        super();
    }

    public DriverPassenger(Driver driver, Passenger passenger) {
        this.driver = driver;
        this.passenger = passenger;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}