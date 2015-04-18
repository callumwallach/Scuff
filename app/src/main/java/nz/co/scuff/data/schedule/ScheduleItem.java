package nz.co.scuff.data.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import nz.co.scuff.data.family.Parent;
import nz.co.scuff.data.school.Route;

/**
 * Created by Callum on 17/03/2015.
 */
public class ScheduleItem implements Parcelable {

    private Parent driver;
    private Route route;
    private DateTime date;

    public ScheduleItem(Parent driver, Route route, DateTime date) {
        this.driver = driver;
        this.route = route;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleItem that = (ScheduleItem) o;

        if (!driver.equals(that.driver)) return false;
        if (!route.equals(that.route)) return false;
        return date.equals(that.date);

    }

    @Override
    public int hashCode() {
        int result = driver.hashCode();
        result = 31 * result + route.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

    public Parent getDriver() {

        return driver;
    }

    public void setDriver(Parent driver) {
        this.driver = driver;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    protected ScheduleItem(Parcel in) {
        driver = (Parent) in.readValue(Parent.class.getClassLoader());
        route = (Route) in.readValue(Route.class.getClassLoader());
        date = (DateTime) in.readValue(DateTime.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(driver);
        dest.writeValue(route);
        dest.writeValue(date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ScheduleItem> CREATOR = new Parcelable.Creator<ScheduleItem>() {
        @Override
        public ScheduleItem createFromParcel(Parcel in) {
            return new ScheduleItem(in);
        }

        @Override
        public ScheduleItem[] newArray(int size) {
            return new ScheduleItem[size];
        }
    };
}