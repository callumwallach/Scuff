package nz.co.scuff.data.school;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Child;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="ChildSchools")
public class ChildSchool extends Model implements Serializable {

    @Column(name = "ChildFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Child child;
    @Column(name = "SchoolFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private School school;

    public ChildSchool() {
        super();
    }

    public ChildSchool(Child child, School school) {
        this.child = child;
        this.school = school;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}