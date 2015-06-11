package nz.co.scuff.data.institution;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import nz.co.scuff.data.base.Coordinator;

/**
 * Created by Callum on 3/06/2015.
 */
@Table(name="InstitutionDatas")
public class InstitutionData extends Model implements Comparable, Parcelable {

    @Expose
    @Column(name="Name")
    private String name;
    @Expose
    @Column(name="Email")
    private String email;
    @Expose
    @Column(name="Phone")
    private String phone;

/*    // for use with ActiveAndroid. Not serialised
    @Column(name="InstitutionFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private Coordinator institution;*/

    public InstitutionData() {
    }

    public InstitutionData(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String firstName) {
        this.name = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

/*    public Coordinator getInstitution() {
        return institution;
    }

    public void setInstitution(Coordinator institution) {
        this.institution = institution;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstitutionData that = (InstitutionData) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return !(phone != null ? !phone.equals(that.phone) : that.phone != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object another) {
        InstitutionData that = (InstitutionData)another;
        if (that.name == null) return 1;
        if (this.name == null) return -1;
        return this.name.compareTo(that.name);
    }

    @Override
    public String toString() {
        return "InstitutionData{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                "} " + super.toString();
    }

    protected InstitutionData(Parcel in) {
        name = in.readString();
        email = in.readString();
        phone = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<InstitutionData> CREATOR = new Parcelable.Creator<InstitutionData>() {
        @Override
        public InstitutionData createFromParcel(Parcel in) {
            return new InstitutionData(in);
        }

        @Override
        public InstitutionData[] newArray(int size) {
            return new InstitutionData[size];
        }
    };

}
