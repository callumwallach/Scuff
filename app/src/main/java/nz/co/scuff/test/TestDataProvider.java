package nz.co.scuff.test;

import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.ChildParent;
import nz.co.scuff.data.family.Parent;
import nz.co.scuff.data.family.Person;
import nz.co.scuff.data.school.ChildSchool;
import nz.co.scuff.data.school.ParentRoute;
import nz.co.scuff.data.school.ParentSchool;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 18/03/2015.
 */
public class TestDataProvider {

    private static long PARENT_ID = 1;
    static long getNewParentId() {
        return PARENT_ID++;
    }
    private static long ROUTE_ID = 1;
    static long getNewRouteId() {
        return ROUTE_ID++;
    }
    private static long CHILD_ID = 1;
    static long getNewChildId() {
        return CHILD_ID++;
    }
    private static long SCHOOL_ID = 1;
    static long getNewSchoolId() {
        return SCHOOL_ID++;
    }

    private static void populate() {

        School school = new School("St Heliers School", -36.8582550, 174.8608230, 42.16);
        school.setSchoolId(getNewSchoolId());
        school.save();

        Route route1 = new Route("Long Drive", "longdrive.png");
        route1.setRouteId(getNewRouteId());
        Route route2 = new Route("St Heliers Bay", "stheliersbayroad.png");
        route2.setRouteId(getNewRouteId());
        Route route3 = new Route("Riddell Nth", "riddellroadnorth.png");
        route3.setRouteId(getNewRouteId());
        Route route4 = new Route("Riddell Sth", "riddellroadsouth.png");
        route4.setRouteId(getNewRouteId());

        route1.setSchool(school);
        route2.setSchool(school);
        route3.setSchool(school);
        route4.setSchool(school);

        route1.save();
        route2.save();
        route3.save();
        route4.save();

        school.addRoute(route1);
        school.addRoute(route2);
        school.addRoute(route3);
        school.addRoute(route4);

        school.save();

        Child child1 = new Child("Cayden", "Lin-Vaile", Person.Gender.MALE);
        child1.setChildId(getNewChildId());
        Child child2 = new Child("Connor", "Lin", Person.Gender.MALE);
        child2.setChildId(getNewChildId());
        Child child3 = new Child("Mia", "Lin", Person.Gender.FEMALE);
        child3.setChildId(getNewChildId());

        child1.save();
        child2.save();
        child3.save();

        ChildSchool childSchool1 = new ChildSchool(child1, school);
        ChildSchool childSchool2 = new ChildSchool(child2, school);
        ChildSchool childSchool3 = new ChildSchool(child3, school);

        childSchool1.save();
        childSchool2.save();
        childSchool3.save();

        child1.addChildSchool(childSchool1);
        child2.addChildSchool(childSchool2);
        child3.addChildSchool(childSchool3);

        child1.save();
        child2.save();
        child3.save();

        Parent parent1 = new Parent("Christine", "Lin", Person.Gender.FEMALE);
        parent1.setParentId(getNewParentId());
        parent1.setEmail("christine@gmail.com");
        parent1.setPhone("021666377");
        Parent parent2 = new Parent("Callum", "Wallach", Person.Gender.MALE);
        parent2.setParentId(getNewParentId());
        parent2.setEmail("callum@gmail.com");
        parent2.setPhone("021658093");

        parent1.save();
        parent2.save();

        ChildParent childParent1 = new ChildParent(child1, parent1);
        ChildParent childParent2 = new ChildParent(child2, parent1);
        ChildParent childParent3 = new ChildParent(child3, parent1);
        ChildParent childParent4 = new ChildParent(child1, parent2);
        ChildParent childParent5 = new ChildParent(child2, parent2);
        ChildParent childParent6 = new ChildParent(child3, parent2);

        childParent1.save();
        childParent2.save();
        childParent3.save();
        childParent4.save();
        childParent5.save();
        childParent6.save();

        child1.addChildParent(childParent1);
        child1.addChildParent(childParent4);

        child2.addChildParent(childParent2);
        child2.addChildParent(childParent5);

        child3.addChildParent(childParent3);
        child3.addChildParent(childParent6);

        parent1.addChildParent(childParent1);
        parent1.addChildParent(childParent2);
        parent1.addChildParent(childParent3);

        parent2.addChildParent(childParent4);
        parent2.addChildParent(childParent5);
        parent2.addChildParent(childParent6);

        ParentSchool parentSchool1 = new ParentSchool(parent1, school);
        ParentSchool parentSchool2 = new ParentSchool(parent2, school);

        parentSchool1.save();
        parentSchool2.save();

        parent1.addParentSchool(parentSchool1);
        parent2.addParentSchool(parentSchool2);

        school.addParentSchool(parentSchool1);
        school.addParentSchool(parentSchool2);

        school.save();

        ParentRoute parentRoute1 = new ParentRoute(parent1, route1);
        ParentRoute parentRoute2 = new ParentRoute(parent2, route1);
        ParentRoute parentRoute3 = new ParentRoute(parent2, route2);

        parentRoute1.save();
        parentRoute2.save();
        parentRoute3.save();

        parent1.addParentRoute(parentRoute1);
        parent2.addParentRoute(parentRoute2);
        parent2.addParentRoute(parentRoute3);

        parent1.save();
        parent2.save();

    }

    public static void populateTestData() {
        populate();
    }

}
