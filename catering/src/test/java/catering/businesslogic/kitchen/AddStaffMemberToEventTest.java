package catering.businesslogic.kitchen;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.staff.StaffMember;
import catering.businesslogic.staff.Team;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

@TestMethodOrder(OrderAnnotation.class)
public class AddStaffMemberToEventTest {

    private static final Logger LOGGER = LogManager.getLogger(AddStaffMemberToEventTest.class);

    private static CatERing app;
    private static User organizer;
    private static User nonOrganizer;
    private static Event testEvent;

    @BeforeAll
    static void init() {
        PersistenceManager.initializeDatabase("database/catering_init_sqlite.sql");
        app = CatERing.getInstance();
    }

    @BeforeEach
    void setup() {
        // Organizer user
        organizer = User.load("Francesca");
        assertNotNull(organizer, "Organizer user should be loaded");
        assertTrue(organizer.isOrganizer(), "User should be an organizer");

        // Non-organizer user
        nonOrganizer = User.load("Antonio");
        assertNotNull(nonOrganizer, "Non-organizer user should be loaded");
        assertFalse(nonOrganizer.isOrganizer(), "User should not be an organizer");

        // Load event
        testEvent = Event.loadByName("Gala Aziendale Annuale");
        assertNotNull(testEvent, "Event should be loaded");
    }

    @Test
    @Order(1)
    void testAddMemberToEvent_Success() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        // Crea o associa un team all’evento
        Team team = testEvent.getTeam();
        if (team == null) {
            team = new Team();
            testEvent.setTeam(team);
        }

        List<String> ruoli = new ArrayList<>();
        ruoli.add("Cameriere");
        StaffMember newMember = new StaffMember(null, "Laura", ruoli, false);

        try {
            StaffMember added = app.getStaffManager().addNewMemberForTheEvent(newMember, testEvent);
            assertNotNull(added, "Added member should not be null");
            assertEquals("Laura", added.getNominativo(), "Name should match");
            assertTrue(testEvent.getTeam().getMembers().contains(added), "Team should contain new member");

            LOGGER.info("Staff member added successfully: " + added.getNominativo());
        } catch (UseCaseLogicException e) {
            fail("Exception should not be thrown in successful case: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testAddMember_NullEvent_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        List<String> ruoli = new ArrayList<>();
        ruoli.add("Cameriere");
        StaffMember newMember = new StaffMember(null, "Laura", ruoli, false);

        assertThrows(UseCaseLogicException.class, () -> {
            app.getStaffManager().addNewMemberForTheEvent(newMember, null);
        }, "Should throw UseCaseLogicException if event is null");
    }

    @Test
    @Order(3)
    void testAddMember_NotOrganizer_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(nonOrganizer.getUserName());

        List<String> ruoli = new ArrayList<>();
        ruoli.add("Cameriere");
        StaffMember newMember = new StaffMember(null, "Laura", ruoli, false);

        assertThrows(UseCaseLogicException.class, () -> {
            app.getStaffManager().addNewMemberForTheEvent(newMember, testEvent);
        }, "Should throw UseCaseLogicException if user is not organizer");
    }
}
