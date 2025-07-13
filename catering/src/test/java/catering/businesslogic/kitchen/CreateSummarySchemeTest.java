package catering.businesslogic.kitchen;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.event.EventManager;
import catering.businesslogic.event.SummaryScheme;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateSummarySchemeTest {

    private static final Logger LOGGER = LogManager.getLogger(CreateSummarySchemeTest.class);
    private static CatERing app;
    private static EventManager eventManager;
    private static Event testEvent;
    private static User organizer;
    private static User nonOrganizer;
    private static SummaryScheme testSummaryScheme;

    @BeforeAll
    static void init() throws UseCaseLogicException {
        PersistenceManager.initializeDatabase("database/catering_init_sqlite.sql");
        app = CatERing.getInstance();
    }

    @BeforeEach
    void setup() throws UseCaseLogicException {
        eventManager = app.getEventManager();

        organizer = User.load("Francesca"); // organizer user
        assertNotNull(organizer, "Organizer user should be loaded");
        assertTrue(organizer.isOrganizer(), "User should be an organizer");

        nonOrganizer = User.load("Antonio"); // non-organizer user
        assertNotNull(nonOrganizer, "Non-organizer user should be loaded");
        assertFalse(nonOrganizer.isOrganizer(), "User should not be an organizer");

        testEvent = Event.loadByName("Smith Wedding"); //id == 1 tested
        assertNotNull(testEvent, "Event should be loaded");

        testSummaryScheme = SummaryScheme.loadByEventId(testEvent.getId()); 
        assertNotNull(testSummaryScheme, "SummaryScheme should be loaded"); //tested, loads smith wedding correctly

    }

    @Test
    @Order(1)
    void testLoadSummaryScheme_Success() throws UseCaseLogicException {
         // Login come organizer
        app.getUserManager().fakeLogin(organizer.getUserName());

        app.getEventManager().setSelectedEvent(testEvent);
        assertNotNull(eventManager.getSelectedEvent(), "Selected event should be set");

        int nrOfStaffMembersRequired = 8;
        String transportationNeeds = "Shuttle bus from Porta Nuova Turin station to the place of destination";
        String typeOfService = "Wedding catering";
        String clientRequest = "Vegetarian menu required, gluten-free options";    

        SummaryScheme ss = eventManager.createSummaryScheme(
        nrOfStaffMembersRequired, transportationNeeds, typeOfService, clientRequest);
        assertNotNull(ss, "SummaryScheme should be loaded from DB");
        assertEquals(8, ss.getNrOfStaffMembersRequired(), "Staff required should match");
        assertEquals("Shuttle bus from Porta Nuova Turin station to the place of destination", 
                    ss.getTransportationNeeds(), "Transportation needs should match");
        assertEquals("Wedding catering", ss.getTypeOfService(), "Type of service should match");
        assertEquals("Vegetarian menu required, gluten-free options", 
                    ss.getClientRequest(), "Client request should match");


        LOGGER.info("Created SummaryScheme: " + ss);
    }

    @Test
    @Order(2)
    void testCreateSummaryScheme_NoEventSelected_Throws() throws UseCaseLogicException {
         app.getUserManager().fakeLogin(organizer.getUserName());

        eventManager.setSelectedEvent(null); 

        UseCaseLogicException thrown = assertThrows(UseCaseLogicException.class, () -> {
            eventManager.createSummaryScheme(3, "none", "plated", "gluten free");
        });

        assertEquals("Nessun evento selezionato di cui creare il summary scheme", thrown.getMessage());
    }

    @Test
    @Order(3)
    void testCreateSummaryScheme_NonOrganizer_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(nonOrganizer.getUserName());

        eventManager = app.getEventManager();
        eventManager.setSelectedEvent(testEvent);

        UseCaseLogicException thrown = assertThrows(UseCaseLogicException.class, () -> {
            eventManager.createSummaryScheme(2, "bus", "buffet", "vegan");
        });

        assertEquals("The User is not an organizer, you can't create a summary scheme",
                    thrown.getMessage());
    }

}
