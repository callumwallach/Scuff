package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Callum on 17/03/2015.
 */
public class Parent extends Person implements Parcelable {

    private String email;
    private String phone;

    public Parent(String name) {
        super(name);
    }

    public Parent(String name, Gender gender) {
        super(name, gender);
    }

    public Parent(String name, Gender gender, String pix) {
        super(name, gender, pix);
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

    protected Parent(Parcel in) {
        super(in);
        email = in.readString();
        phone = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(phone);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Parent> CREATOR = new Parcelable.Creator<Parent>() {
        @Override
        public Parent createFromParcel(Parcel in) {
            return new Parent(in);
        }

        @Override
        public Parent[] newArray(int size) {
            return new Parent[size];
        }
    };
}