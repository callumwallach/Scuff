package nz.co.scuff.data.school;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Parent;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="ParentRoutes")
public class ParentRoute extends Model implements Serializable {

    @Column(name = "ParentFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Parent parent;
    @Column(name = "RouteFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Route route;

    public ParentRoute() {
        super();
    }

    public ParentRoute(Parent parent, Route route) {
        this.parent = parent;
        this.route = route;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

}