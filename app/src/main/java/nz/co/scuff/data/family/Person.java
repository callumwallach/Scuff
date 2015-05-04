package nz.co.scuff.data.family;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Callum on 17/03/2015.
 */
public abstract class Person extends Model implements Comparable, Serializable {

    public enum Gender {
        MALE, FEMALE
    }

    @Expose
    @Column(name="FirstName")
    protected String firstName;
    @Expose
    @Column(name="MiddleName")
    protected String middleName;
    @Expose
    @Column(name="LastName")
    protected String lastName;
    @Expose
    @Column(name="Gender")
    protected Gender gender;
    @Expose
    @Column(name="Picture")
    protected String picture;

    public Person() {}

    public Person(String firstName, String lastName, Gender gender) {
        this(firstName, lastName, gender, null);
    }

    public Person(String firstName, String lastName, Gender gender, String picture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.picture = picture;
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

    @Override
    public int compareTo(Object another) {
        Person other = (Person)another;
        int lastNameCompared = this.lastName.compareTo(other.lastName);
        if (lastNameCompared != 0) return lastNameCompared;
        return this.firstName.compareTo(other.firstName);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Person{");
        sb.append("firstName='").append(firstName).append('\'');
        sb.append(", middleName='").append(middleName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", gender=").append(gender);
        sb.append(", picture='").append(picture).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
