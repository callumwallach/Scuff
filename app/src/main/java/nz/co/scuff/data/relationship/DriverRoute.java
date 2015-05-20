package nz.co.scuff.data.relationship;

import android.os.Parcel;
import android.os.Parcelable;

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
public class DriverRoute extends Model implements Serializable, Parcelable {

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

    protected DriverRoute(Parcel in) {
        driver = (Driver) in.readValue(Driver.class.getClassLoader());
        route = (Route) in.readValue(Route.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(driver);
        dest.writeValue(route);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DriverRoute> CREATOR = new Parcelable.Creator<DriverRoute>() {
        @Override
        public DriverRoute createFromParcel(Parcel in) {
            return new DriverRoute(in);
        }

        @Override
        public DriverRoute[] newArray(int size) {
            return new DriverRoute[size];
        }
    };
}