package nz.co.scuff.data.family;

import com.google.gson.annotations.Expose;

/**
 * Created by Callum on 4/05/2015.
 */
public class ParentSnapshot extends PersonSnapshot {

    @Expose
    private String email;
    @Expose
    private String phone;

    public ParentSnapshot() {
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

    @Override
    public String toString() {
        return "ParentSnapshot{" +
                "email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                "} " + super.toString();
    }
}
