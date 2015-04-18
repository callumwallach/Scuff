package nz.co.scuff.data.school;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Callum on 17/03/2015.
 */
public class Route implements Parcelable {

    private String name;
    private Bus bus;
    private String routeMap;

    public Route(String name) {
        this.name = name;
        this.bus = new Bus(name);
        this.routeMap = "Currently not available";
    }

    public Route(String name, String routeMap) {
        this.name = name;
        this.bus = new Bus(name);
        this.routeMap = routeMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public String getRouteMap() {
        return routeMap;
    }

    public void setRouteMap(String routeMap) {
        this.routeMap = routeMap;
    }

    @Override
    public String toString() {
        return name;
    }

    protected Route(Parcel in) {
        name = in.readString();
        bus = (Bus) in.readValue(Bus.class.getClassLoader());
        routeMap = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(bus);
        dest.writeString(routeMap);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}