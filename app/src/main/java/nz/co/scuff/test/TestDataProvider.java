package nz.co.scuff.test;

import nz.co.scuff.data.relationship.DriverPassenger;
import nz.co.scuff.data.family.Person;
import nz.co.scuff.data.relationship.PassengerSchool;
import nz.co.scuff.data.relationship.DriverRoute;
import nz.co.scuff.data.relationship.DriverSchool;
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

/*        School school = new School("St Heliers School", -36.8582550, 174.8608230, 42.16);
        school.setSchool(getNewSchoolId());
        school.save();

        Route route1 = new Route("Long Drive", "longdrive.png");
        route1.setRoute(getNewRouteId());
        Route route2 = new Route("St Heliers Bay", "stheliersbayroad.png");
        route2.setRoute(getNewRouteId());
        Route route3 = new Route("Riddell Nth", "riddellroadnorth.png");
        route3.setRoute(getNewRouteId());
        Route route4 = new Route("Riddell Sth", "riddellroadsouth.png");
        route4.setRoute(getNewRouteId());

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

        PassengerSchool passengerSchool1 = new PassengerSchool(child1, school);
        PassengerSchool passengerSchool2 = new PassengerSchool(child2, school);
        PassengerSchool passengerSchool3 = new PassengerSchool(child3, school);

        passengerSchool1.save();
        passengerSchool2.save();
        passengerSchool3.save();

        child1.addChildSchool(passengerSchool1);
        child2.addChildSchool(passengerSchool2);
        child3.addChildSchool(passengerSchool3);

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

        DriverPassenger driverPassenger1 = new DriverPassenger(child1, parent1);
        DriverPassenger driverPassenger2 = new DriverPassenger(child2, parent1);
        DriverPassenger driverPassenger3 = new DriverPassenger(child3, parent1);
        DriverPassenger driverPassenger4 = new DriverPassenger(child1, parent2);
        DriverPassenger driverPassenger5 = new DriverPassenger(child2, parent2);
        DriverPassenger driverPassenger6 = new DriverPassenger(child3, parent2);

        driverPassenger1.save();
        driverPassenger2.save();
        driverPassenger3.save();
        driverPassenger4.save();
        driverPassenger5.save();
        driverPassenger6.save();

        child1.addChildParent(driverPassenger1);
        child1.addChildParent(driverPassenger4);

        child2.addChildParent(driverPassenger2);
        child2.addChildParent(driverPassenger5);

        child3.addChildParent(driverPassenger3);
        child3.addChildParent(driverPassenger6);

        parent1.addChildParent(driverPassenger1);
        parent1.addChildParent(driverPassenger2);
        parent1.addChildParent(driverPassenger3);

        parent2.addChildParent(driverPassenger4);
        parent2.addChildParent(driverPassenger5);
        parent2.addChildParent(driverPassenger6);

        DriverSchool driverSchool1 = new DriverSchool(parent1, school);
        DriverSchool driverSchool2 = new DriverSchool(parent2, school);

        driverSchool1.save();
        driverSchool2.save();

        parent1.addParentSchool(driverSchool1);
        parent2.addParentSchool(driverSchool2);

        school.addParentSchool(driverSchool1);
        school.addParentSchool(driverSchool2);

        school.save();

        DriverRoute driverRoute1 = new DriverRoute(parent1, route1);
        DriverRoute driverRoute2 = new DriverRoute(parent2, route1);
        DriverRoute driverRoute3 = new DriverRoute(parent2, route2);

        driverRoute1.save();
        driverRoute2.save();
        driverRoute3.save();

        parent1.addParentRoute(driverRoute1);
        parent2.addParentRoute(driverRoute2);
        parent2.addParentRoute(driverRoute3);

        parent1.save();
        parent2.save();*/

    }

    public static void populateTestData() {
        populate();
    }

}
