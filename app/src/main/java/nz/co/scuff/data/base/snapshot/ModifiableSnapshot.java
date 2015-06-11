package nz.co.scuff.data.base.snapshot;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;

/**
 * Created by Callum on 3/06/2015.
 */
public abstract class ModifiableSnapshot implements Snapshot {

    @Expose
    protected boolean active;
    @Expose
    protected Timestamp lastModified;

    public ModifiableSnapshot() {
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
        return "ModifiableSnapshot{" +
                "active=" + active +
                ", lastModified=" + lastModified +
                '}';
    }



}
