package nz.co.scuff.data.util;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

import nz.co.scuff.data.family.snapshot.AdultSnapshot;
import nz.co.scuff.data.family.snapshot.ChildSnapshot;
import nz.co.scuff.data.institution.snapshot.InstitutionSnapshot;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.journey.snapshot.StampSnapshot;
import nz.co.scuff.data.journey.snapshot.TicketSnapshot;
import nz.co.scuff.data.journey.snapshot.WaypointSnapshot;
import nz.co.scuff.data.institution.snapshot.RouteSnapshot;
import nz.co.scuff.data.place.snapshot.PlaceSnapshot;

/**
 * Created by Callum on 11/05/2015.
 */
public class DataPacket {

    @Expose
    Map<Long, PlaceSnapshot> placeSnapshots;
    @Expose
    Map<Long, InstitutionSnapshot> institutionSnapshots;
    @Expose
    Map<Long, RouteSnapshot> routeSnapshots;
    @Expose
    Map<String, JourneySnapshot> journeySnapshots;
    @Expose
    Map<String, WaypointSnapshot> waypointSnapshots;
    @Expose
    Map<Long, ChildSnapshot> childSnapshots;
    @Expose
    Map<Long, AdultSnapshot> adultSnapshots;
    @Expose
    Map<Long, TicketSnapshot> ticketSnapshots;
    @Expose
    Map<Long, StampSnapshot> stampSnapshots;

    public DataPacket() {
        this.placeSnapshots = new HashMap<>();
        this.institutionSnapshots = new HashMap<>();
        this.routeSnapshots = new HashMap<>();
        this.journeySnapshots = new HashMap<>();
        this.waypointSnapshots = new HashMap<>();
        this.childSnapshots = new HashMap<>();
        this.adultSnapshots = new HashMap<>();
        this.ticketSnapshots = new HashMap<>();
        this.stampSnapshots = new HashMap<>();
    }

    public Map<Long, PlaceSnapshot> getPlaceSnapshots() {
        return placeSnapshots;
    }

    public void setPlaceSnapshots(Map<Long, PlaceSnapshot> placeSnapshots) {
        this.placeSnapshots = placeSnapshots;
    }

    public Map<Long, InstitutionSnapshot> getInstitutionSnapshots() {
        return institutionSnapshots;
    }

    public void setInstitutionSnapshots(Map<Long, InstitutionSnapshot> institutionSnapshots) {
        this.institutionSnapshots = institutionSnapshots;
    }

    public Map<Long, RouteSnapshot> getRouteSnapshots() {
        return routeSnapshots;
    }

    public void setRouteSnapshots(Map<Long, RouteSnapshot> routeSnapshots) {
        this.routeSnapshots = routeSnapshots;
    }

    public Map<String, JourneySnapshot> getJourneySnapshots() {
        return journeySnapshots;
    }

    public void setJourneySnapshots(Map<String, JourneySnapshot> journeySnapshots) {
        this.journeySnapshots = journeySnapshots;
    }

    public Map<Long, ChildSnapshot> getChildSnapshots() {
        return childSnapshots;
    }

    public void setChildSnapshots(Map<Long, ChildSnapshot> childSnapshots) {
        this.childSnapshots = childSnapshots;
    }

    public Map<Long, AdultSnapshot> getAdultSnapshots() {
        return adultSnapshots;
    }

    public void setAdultSnapshots(Map<Long, AdultSnapshot> adultSnapshots) {
        this.adultSnapshots = adultSnapshots;
    }

    public Map<String, WaypointSnapshot> getWaypointSnapshots() {
        return waypointSnapshots;
    }

    public void setWaypointSnapshots(Map<String, WaypointSnapshot> waypointSnapshots) {
        this.waypointSnapshots = waypointSnapshots;
    }

    public Map<Long, TicketSnapshot> getTicketSnapshots() {
        return ticketSnapshots;
    }

    public void setTicketSnapshots(Map<Long, TicketSnapshot> ticketSnapshots) {
        this.ticketSnapshots = ticketSnapshots;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "placeSnapshots=" + placeSnapshots +
                ", institutionSnapshots=" + institutionSnapshots +
                ", routeSnapshots=" + routeSnapshots +
                ", journeySnapshots=" + journeySnapshots +
                ", waypointSnapshots=" + waypointSnapshots +
                ", childSnapshots=" + childSnapshots +
                ", adultSnapshots=" + adultSnapshots +
                ", ticketSnapshots=" + ticketSnapshots +
                ", stampSnapshots=" + stampSnapshots +
                '}';
    }

    public Map<Long, StampSnapshot> getStampSnapshots() {
        return stampSnapshots;
    }

    public void setStampSnapshots(Map<Long, StampSnapshot> stampSnapshots) {
        this.stampSnapshots = stampSnapshots;
    }

}
