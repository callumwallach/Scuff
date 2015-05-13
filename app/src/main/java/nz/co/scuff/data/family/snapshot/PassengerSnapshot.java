package nz.co.scuff.data.family.snapshot;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Callum on 4/05/2015.
 */
public class PassengerSnapshot extends PersonSnapshot {

    @Expose
    private Set<Long> schoolIds;
    @Expose
    private Set<Long> parentIds;
    @Expose
    private Set<String> boardingInfoIds;
    @Expose
    private Set<Long> registeredRouteIds;

    public PassengerSnapshot() {
        schoolIds = new HashSet<>();
        parentIds = new HashSet<>();
        boardingInfoIds = new HashSet<>();
        registeredRouteIds = new HashSet<>();
    }

    public Set<Long> getSchoolIds() {
        return schoolIds;
    }

    public void setSchools(Set<Long> schoolIds) {
        this.schoolIds = schoolIds;
    }

    public Set<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(Set<Long> parentIds) {
        this.parentIds = parentIds;
    }

    public Set<String> getBoardingInfoIds() {
        return boardingInfoIds;
    }

    public void setBoardingInfoIds(Set<String> boardingInfoIds) {
        this.boardingInfoIds = boardingInfoIds;
    }

    public Set<Long> getRegisteredRouteIds() {
        return registeredRouteIds;
    }

    public void setRegisteredRouteIds(Set<Long> registeredRouteIds) {
        this.registeredRouteIds = registeredRouteIds;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PassengerSnapshot{");
        sb.append("schoolIds=").append(schoolIds);
        sb.append(", parentIds=").append(parentIds);
        sb.append(", boardingInfoIds=").append(boardingInfoIds);
        sb.append(", registeredRouteIds=").append(registeredRouteIds);
        sb.append('}');
        return sb.toString();
    }
}
