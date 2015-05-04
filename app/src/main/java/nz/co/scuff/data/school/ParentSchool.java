package nz.co.scuff.data.school;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Parent;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="ParentSchools")
public class ParentSchool extends Model implements Serializable {

    @Column(name = "ParentFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Parent parent;
    @Column(name = "SchoolFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private School school;

    public ParentSchool() {
        super();
    }

    public ParentSchool(Parent parent, School school) {
        this.parent = parent;
        this.school = school;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}