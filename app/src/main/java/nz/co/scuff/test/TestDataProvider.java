package nz.co.scuff.test;

import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.family.Parent;
import nz.co.scuff.data.family.Person;
import nz.co.scuff.data.schedule.Schedule;
import nz.co.scuff.data.schedule.ScheduleItem;
import nz.co.scuff.data.school.Bus;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 18/03/2015.
 */
public class TestDataProvider {

    private static Set<School> schools = new HashSet<School>();
    private static Set<Family> families = new HashSet<Family>();

    private static School populateSchool() {

        School school = new School("St Heliers School", -36.8582550, 174.8608230, 42.16);
        Route route1 = new Route("Long Drive", "longdrive.png");
        Route route2 = new Route("St Heliers Bay", "stheliersbayroad.png");
        Route route3 = new Route("Riddell Nth", "riddellroadnorth.png");
        Route route4 = new Route("Riddell Sth", "riddellroadsouth.png");

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

        Family family = new Family("Lin");
        Child child1 = new Child("Cayden", Person.Gender.MALE);
        Child child2 = new Child("Mia", Person.Gender.FEMALE);
        Parent parent1 = new Parent("Christine", Person.Gender.FEMALE);
        Parent parent2 = new Parent("Callum", Person.Gender.MALE);
        family.addChild(child1);
        family.addChild(child2);
        family.addParent(parent1);
        family.addParent(parent2);
        School school = populateSchool();
        family.addSchool(school);

        storeFamily(family);

        return family;
    }

    private static Schedule populateSchedule(Parent driver, Route route, DateTime datetime) {

        Schedule schedule = new Schedule();
        ScheduleItem item = new ScheduleItem(driver, route, datetime);
        schedule.addScheduleItem(item);

        return schedule;
    }

    public static void populateTestData() {
        Family family = populateFamily();
        School school = populateSchool();
        Schedule schedule = school.getSchedule();
        ScheduleItem item = new ScheduleItem(family.getParents().iterator().next(), school.getRoutes().iterator().next(), DateTime.now());
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
