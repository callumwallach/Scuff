package de.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class ScuffGenerator {

    static Schema schema = new Schema(1, "nz.co.scuff.data.entity");

    static Entity place = schema.addEntity("Place");
    static Entity adult = schema.addEntity("Adult");
    static Entity child = schema.addEntity("Child");
    static Entity route = schema.addEntity("Route");
    static Entity stamp = schema.addEntity("Stamp");
    static Entity ticket = schema.addEntity("Ticket");
    static Entity journey = schema.addEntity("Journey");
    static Entity waypoint = schema.addEntity("Waypoint");
    static Entity institution = schema.addEntity("Institution");
    static Entity personalData = schema.addEntity("PersonalData");
    static Entity institutionData = schema.addEntity("InstitutionData");

    public static void main(String args[]) throws Exception {

        schema.enableKeepSectionsByDefault();

        // stamp
        stamp.addIdProperty();
        stamp.addLongProperty("stampId");
        stamp.addDoubleProperty("latitude");
        stamp.addDoubleProperty("longitude");
        stamp.addDateProperty("stampDate");
        addSnapshotable(stamp);

        // place
        place.addIdProperty();
        place.addLongProperty("placeId");
        place.addStringProperty("name");
        place.addDoubleProperty("latitude");
        place.addDoubleProperty("longitude");
        place.addDoubleProperty("altitude");
        addModifiableEntity(place);

        // route
        route.addIdProperty();
        route.addLongProperty("routeId");
        routeName = route.addStringProperty("name").getProperty();
        route.addStringProperty("routeMap");
        routePlaceId = route.addLongProperty("originId").getProperty();
        route.addToOne(place, routePlaceId, "origin");
        routeDestinationId = route.addLongProperty("destinationId").getProperty();
        route.addToOne(place, routeDestinationId, "destination");
        routeAdultOwnerId = route.addLongProperty("adultOwnerId").getProperty();
        route.addToOne(adult, routeAdultOwnerId, "adultOwner");
        routeInstitutionOwnerId = route.addLongProperty("institutionOwnerId").getProperty();
        route.addToOne(institution, routeInstitutionOwnerId, "institutionOwner");
        addModifiableEntity(route);

        // ticket
        ticket.addIdProperty();
        ticket.addLongProperty("ticketId");
        Property ticketIssueDate = ticket.addDateProperty("issueDate").getProperty();
        Property ticketStampId = ticket.addLongProperty("stampId").getProperty();
        ticket.addToOne(stamp, ticketStampId);
        Property ticketJourneyId = ticket.addLongProperty("journeyId").getProperty();
        ticket.addToOne(journey, ticketJourneyId);
        Property ticketChildId = ticket.addLongProperty("childId").getProperty();
        ticket.addToOne(child, ticketChildId);
        addModifiableEntity(ticket);

        // adult
        adult.addIdProperty();
        Property adultPersonalDataId = adult.addLongProperty("personalDataId").getProperty();
        adult.addToOne(personalData, adultPersonalDataId);
        // TODO many to many
        // children
        // guidees
        addCoordinator(adult);

        // child
        child.addIdProperty();
        child.addLongProperty("childId");
        // one to many
        // tickets
        ToMany childToTickets = child.addToMany(ticket, ticketChildId);
        childToTickets.setName("tickets");
        childToTickets.orderDesc(ticketIssueDate);
        // TODO many to many
        // parents
        addModifiableEntity(child);

        // institution
        institution.addIdProperty();
        Property institutionPersonalDataId = institution.addLongProperty("institutionDataId").getProperty();
        institution.addToOne(personalData, institutionPersonalDataId);
        // TODO many to many
        // guides
        addCoordinator(institution);

        // waypoint
        waypoint.addLongProperty("waypointId");
        waypoint.addDoubleProperty("latitude");
        waypoint.addDoubleProperty("longitude");
        waypoint.addFloatProperty("speed");
        waypoint.addFloatProperty("bearing");
        waypoint.addFloatProperty("distance");
        waypoint.addLongProperty("duration");
        waypoint.addStringProperty("provider");
        waypoint.addFloatProperty("accuracy");
        waypoint.addDoubleProperty("altitude");
        waypoint.addIntProperty("state");
        Property waypointCreated = waypoint.addDateProperty("created").getProperty();
        Property waypointJourneyId = waypoint.addLongProperty("journeyId").getProperty();
        waypoint.addToOne(journey, waypointJourneyId);
        addSnapshotable(waypoint);

        // journey
        journey.addIdProperty();
        journey.addStringProperty("journeyId");
        journey.addStringProperty("appId");
        journey.addStringProperty("source");
        journey.addFloatProperty("totalDistance");
        journey.addLongProperty("totalDuration");
        journey.addDateProperty("created");
        journey.addDateProperty("completed");
        journey.addIntProperty("state");
        /*Property journeyOwnerId = journey.addLongProperty("ownerId").getProperty();
        journey.addToOne(coordinator, journeyOwnerId);
        Property journeyAgentId = journey.addLongProperty("agentId").getProperty();
        journey.addToOne(coordinator, journeyAgentId);*/
        journeyGuideId = journey.addLongProperty("guideId").getProperty();
        journey.addToOne(adult, journeyGuideId);
        journeyOriginId = journey.addLongProperty("originId").getProperty();
        journey.addToOne(place, journeyOriginId, "origin");
        journeyDestinationId = journey.addLongProperty("destinationId").getProperty();
        journey.addToOne(place, journeyDestinationId, "destination");
        // one to many
        // waypoints
        // tickets
        ToMany journeyToWaypoints = journey.addToMany(waypoint, waypointJourneyId);
        journeyToWaypoints.setName("waypoints");
        journeyToWaypoints.orderDesc(waypointCreated);
        ToMany journeyToTickets = journey.addToMany(ticket, ticketJourneyId);
        journeyToTickets.setName("tickets");
        journeyToTickets.orderDesc(ticketIssueDate);

        // personal data
        personalData.addIdProperty();
        personalData.addStringProperty("firstName");
        personalData.addStringProperty("middleName");
        personalData.addStringProperty("lastName");
        personalData.addIntProperty("Gender");
        personalData.addStringProperty("picture");
        personalData.addStringProperty("email");
        personalData.addStringProperty("phone");

        // institution data
        institutionData.addIdProperty();
        institutionData.addStringProperty("name");
        institutionData.addStringProperty("email");
        institutionData.addStringProperty("phone");

        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

    static Property journeyGuideId;
    static Property journeyOriginId;
    static Property journeyDestinationId;

    static Property routeName;
    static Property routePlaceId;
    static Property routeDestinationId;
    static Property routeAdultOwnerId;
    static Property routeInstitutionOwnerId;

    private static  Entity addCoordinator(Entity entity) {

        entity.implementsInterface("nz.co.scuff.data.entity.base.Coordinator");
        entity.addLongProperty("coordinatorId");
        // one to many
        // routes
        ToMany entityToRoutes = entity.addToMany(route, routeAdultOwnerId);
        entityToRoutes.setName("routes");
        entityToRoutes.orderDesc(routeName);
        // TODO many to many
        // places
        // friends
        // pastJourneys
        // currentJourneys
        return addModifiableEntity(entity);

    }

    private static Entity addModifiableEntity(Entity entity) {

        entity.implementsInterface("nz.co.scuff.data.entity.base.ModifiableEntity");
        entity.addBooleanProperty("isActive");
        entity.addDateProperty("lastModified");
        return addSnapshotable(entity);

    }

    private static Entity addSnapshotable(Entity entity) {

        entity.implementsInterface("nz.co.scuff.data.entity.base.Snapshotable");
        return entity;

    }

}
