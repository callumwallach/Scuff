package nz.co.scuff.data.relationship;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.base.Coordinator;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="GuidingRelationships")
public class GuidingRelationship extends Model implements Serializable, Parcelable {

    @Column(name = "AdultFK", onDelete= Column.ForeignKeyAction.CASCADE)
    public Coordinator adult;
    @Column(name = "InstitutionFK", onDelete= Column.ForeignKeyAction.CASCADE)
    public Coordinator institution;

    public GuidingRelationship() {
        super();
    }

    public GuidingRelationship(Coordinator adult, Coordinator institution) {
        this.adult = adult;
        this.institution = institution;
    }

    public Coordinator getAdult() {
        return adult;
    }

    public void setAdult(Coordinator adult) {
        this.adult = adult;
    }

    public Coordinator getInstitution() {
        return institution;
    }

    public void setInstitution(Coordinator institution) {
        this.institution = institution;
    }

    protected GuidingRelationship(Parcel in) {
        adult = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
        institution = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GuidingRelationship that = (GuidingRelationship) o;

        if (!adult.equals(that.adult)) return false;
        return institution.equals(that.institution);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + adult.hashCode();
        result = 31 * result + institution.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GuidingRelationship{" +
                "adult=" + adult.getCoordinatorId() +
                ", institution=" + institution.getCoordinatorId() +
                "} " + super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(adult);
        dest.writeValue(institution);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GuidingRelationship> CREATOR = new Parcelable.Creator<GuidingRelationship>() {
        @Override
        public GuidingRelationship createFromParcel(Parcel in) {
            return new GuidingRelationship(in);
        }

        @Override
        public GuidingRelationship[] newArray(int size) {
            return new GuidingRelationship[size];
        }
    };
}