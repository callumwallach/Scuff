package nz.co.scuff.test;

import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.family.Person;
import nz.co.scuff.data.schedule.Schedule;
import nz.co.scuff.data.schedule.ScheduleItem;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 18/03/2015.
 */
public class TestDataProvider {

    private static Set<School> schools = new HashSet<School>();
    private static Set<Family> families = new HashSet<Family>();

    private static Route route1 = new Route("Long Drive", "longdrive.png");
    private static Route route2 = new Route("St Heliers Bay", "stheliersbayroad.png");
    private static Route route3 = new Route("Riddell Nth", "riddellroadnorth.png");
    private static Route route4 = new Route("Riddell Sth", "riddellroadsouth.png");

    private static School populateSchool() {

        School school = new School("St Heliers School", -36.8582550, 174.8608230, 42.16);

        school.addRoute(route1);
        school.addRoute(route2);
        school.addRoute(route3);
        school.addRoute(route4);

        Schedule schedule = new Schedule();
        school.setSchedule(schedule);

        storeSchool(school);

        return school;
    }

    private static Family populateFamily() {

        School school = populateSchool();

        Family family = new Family("Lin");

        Passenger passenger1 = new Passenger("Cayden", Person.Gender.MALE, school);
        Passenger passenger2 = new Passenger("Mia", Person.Gender.FEMALE, school);
        Driver driver1 = new Driver("Christine", Person.Gender.FEMALE);
        Driver driver2 = new Driver("Callum", Person.Gender.MALE);

        family.addPassenger(passenger1);
        family.addPassenger(passenger2);
        family.addDriver(driver1);
        family.addDriver(driver2);

        driver1.addSchoolRoute(school, route1);
        driver2.addSchoolRoute(school, route2);

        storeFamily(family);

        return family;
    }

    private static Schedule populateSchedule(Driver driver, Route route, DateTime datetime) {

        Schedule schedule = new Schedule();
        ScheduleItem item = new ScheduleItem(driver, route, datetime);
        schedule.addScheduleItem(item);

        return schedule;
    }

    public static void populateTestData() {
        Family family = populateFamily();
        School school = populateSchool();
        Schedule schedule = school.getSchedule();
        ScheduleItem item = new ScheduleItem(family.getDrivers().iterator().next(), school.getRoutes().iterator().next(), DateTime.now());
        schedule.addScheduleItem(item);

    }

/*    public static Family getFamily(String name) {
        return families.
    }*/

    public static Family getFamily() {
        return families.iterator().next();
    }

    public static void storeFamily(Family family) {
        families.remove(family);
        families.add(family);
    }

    public static void storeSchool(School school) {
        schools.remove(school);
        schools.add(school);
    }

}
