package nz.co.scuff.data.relationship;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.school.Route;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="PassengerRoutes")
public class PassengerRoute extends Model implements Serializable {

    @Column(name = "PassengerFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Passenger passenger;
    @Column(name = "RouteFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Route route;

    public PassengerRoute() {
        super();
    }

    public PassengerRoute(Passenger passenger, Route route) {
        this.passenger = passenger;
        this.route = route;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

}