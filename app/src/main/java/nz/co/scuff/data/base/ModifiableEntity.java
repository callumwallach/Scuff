package nz.co.scuff.data.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;

import java.sql.Timestamp;

import nz.co.scuff.android.data.BaseModel;

/**
 * Created by Callum on 3/06/2015.
 */
public abstract class ModifiableEntity extends BaseModel implements Parcelable {

    @Column(name="Active")
    protected boolean active;
    @Column(name="LastModified")
    protected Timestamp lastModified;

    public ModifiableEntity() {
        this.active = true;
        this.lastModified = new Timestamp(System.currentTimeMillis());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isDirty(Timestamp timestamp) {
       return lastModified.after(timestamp);
    }

    @Override
    public String toString() {
        return "ModifiableEntity{" +
                "active=" + active +
                ", lastModified=" + lastModified +
                '}';
    }

    protected ModifiableEntity(Parcel in) {
        active = in.readByte() != 0x00;
        lastModified = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (active ? 0x01 : 0x00));
        dest.writeValue(lastModified);
    }

}
