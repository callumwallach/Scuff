package nz.co.scuff.data.relationship;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.school.Route;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="DriverRoutes")
public class DriverRoute extends Model implements Serializable {

    @Column(name = "DriverFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Driver driver;
    @Column(name = "RouteFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Route route;

    public DriverRoute() {
        super();
    }

    public DriverRoute(Driver driver, Route route) {
        this.driver = driver;
        this.route = route;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

}