package nz.co.scuff.data.family;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="ChildParents")
public class ChildParent extends Model implements Serializable {

    @Column(name = "Child", onDelete= Column.ForeignKeyAction.CASCADE)
    public Child child;
    @Column(name = "Parent", onDelete= Column.ForeignKeyAction.CASCADE)
    public Parent parent;

    public List<Child> children() {
        return getMany(Child.class, "ChildParent");
    }
    public List<Parent> parents() {
        return getMany(Parent.class, "ChildParent");
    }

    public ChildParent() {
        super();
    }

    public ChildParent(Child child, Parent parent) {
        this.child = child;
        this.parent = parent;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}