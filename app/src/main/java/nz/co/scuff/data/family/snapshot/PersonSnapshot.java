package nz.co.scuff.data.family.snapshot;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.family.Person;

/**
 * Created by Callum on 4/05/2015.
 */
public abstract class PersonSnapshot implements Comparable {

    @Expose
    private long personId;
    @Expose
    private String firstName;
    @Expose
    private String middleName;
    @Expose
    private String lastName;
    @Expose
    private Person.Gender gender;
    @Expose
    private String picture;

    public PersonSnapshot() {
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
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

    public Person.Gender getGender() {
        return gender;
    }

    public void setGender(Person.Gender gender) {
        this.gender = gender;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonSnapshot that = (PersonSnapshot) o;

        return personId == that.personId;

    }

    @Override
    public int compareTo(Object another) {
        PersonSnapshot other = (PersonSnapshot)another;
        int lastNameCompared = this.lastName.compareTo(other.lastName);
        if (lastNameCompared != 0) return lastNameCompared;
        return this.firstName.compareTo(other.firstName);
    }

    @Override
    public int hashCode() {
        return (int) (personId ^ (personId >>> 32));
    }

    @Override
    public String toString() {
        return "PersonSnapshot{" +
                "personId=" + personId +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", picture='" + picture + '\'' +
                '}';
    }
}
