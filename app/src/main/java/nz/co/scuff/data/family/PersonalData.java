package nz.co.scuff.data.family;

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
@Table(name="PersonalDatas")
public class PersonalData extends Model implements Comparable, Parcelable {

    public enum Gender {
        MALE(0), FEMALE(1);

        private final int value;

        private Gender(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    @Expose
    @Column(name="FirstName")
    private String firstName;
    @Expose
    @Column(name="MiddleName")
    private String middleName;
    @Expose
    @Column(name="LastName")
    private String lastName;
    @Expose
    @Column(name="Gender")
    private Gender gender;
    @Expose
    @Column(name="Picture")
    private String picture;
    @Expose
    @Column(name="Email")
    private String email;
    @Expose
    @Column(name="Phone")
    private String phone;

/*    // for use with ActiveAndroid. Not serialised
    @Column(name="AdultFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private Coordinator adult;*/

    public PersonalData() {
    }

    public PersonalData(String firstName, String middleName, String lastName, Gender gender, String picture, String email, String phone) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.picture = picture;
        this.email = email;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

/*    public Coordinator getAdult() {
        return adult;
    }

    public void setAdult(Coordinator coordinator) {
        this.adult = coordinator;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonalData that = (PersonalData) o;

        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (gender != that.gender) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return !(phone != null ? !phone.equals(that.phone) : that.phone != null);

    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object another) {
        PersonalData that = (PersonalData)another;
        if (that.lastName == null) return 1;
        if (this.lastName == null) return -1;
        int lastNameCompared = this.lastName.compareTo(that.lastName);
        if (lastNameCompared != 0) return lastNameCompared;
        return this.firstName.compareTo(that.firstName);
    }

    @Override
    public String toString() {
        return "PersonalData{" +
                "firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", picture='" + picture + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    protected PersonalData(Parcel in) {
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        gender = (Gender) in.readValue(Gender.class.getClassLoader());
        picture = in.readString();
        email = in.readString();
        phone = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeValue(gender);
        dest.writeString(picture);
        dest.writeString(email);
        dest.writeString(phone);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PersonalData> CREATOR = new Parcelable.Creator<PersonalData>() {
        @Override
        public PersonalData createFromParcel(Parcel in) {
            return new PersonalData(in);
        }

        @Override
        public PersonalData[] newArray(int size) {
            return new PersonalData[size];
        }
    };
}
