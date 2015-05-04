package nz.co.scuff.data.schedule;

import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Schedules")
public class Schedule implements Serializable {

    private long scheduleId;

    public Schedule() {
    }

    public Schedule(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

}