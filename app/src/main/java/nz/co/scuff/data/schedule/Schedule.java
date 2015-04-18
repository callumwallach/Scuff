package nz.co.scuff.data.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

/**
 * Created by Callum on 17/03/2015.
 */
public class Schedule implements Parcelable {

    public Schedule() {
        this.scheduleItems = new HashSet<ScheduleItem>();
    }

    private HashSet<ScheduleItem> scheduleItems;

    public boolean addScheduleItem(ScheduleItem item) {
        return scheduleItems.add(item);
    }

    public boolean removeScheduleItem(ScheduleItem item) {
        return scheduleItems.remove(item);
    }

    public HashSet<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(HashSet<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    protected Schedule(Parcel in) {
        scheduleItems = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(scheduleItems);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
}