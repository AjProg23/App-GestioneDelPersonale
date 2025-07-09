package catering.businesslogic.kitchen;

import static org.junit.jupiter.api.Assertions.*;

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
    private static StaffMember staffMember;

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

       // Evento
        testEvent = Event.loadByName("Smith Wedding"); 
        assertNotNull(testEvent, "Event should be loaded");
        assertNotNull(testEvent.getTeam(), "Event team should be loaded");

        // StaffMember
        staffMember = StaffMember.loadByName("Luigi Bianchi");
        assertNotNull(staffMember, "StaffMember should be loaded");
    }

    @Test
    @Order(1)
    void testAddMemberToEvent_Success() throws UseCaseLogicException {
    // Login come organizer
    app.getUserManager().fakeLogin(organizer.getUserName());

    // Carica il team dall'evento (già caricato correttamente da DB)
    Team team = testEvent.getTeam();
    assertNotNull(team, "Il team dell'evento dovrebbe essere presente");

    // Rimuove Luigi se già presente per evitare falsi positivi nel test
    if (team.getMembers().stream().anyMatch(m -> m.getId().equals(staffMember.getId()))) {
        team.removeMember(staffMember.getId());
    }

    // Aggiunge il membro Luigi Bianchi all'evento
    StaffMember added = app.getStaffManager().addNewMemberForTheEvent(staffMember, testEvent);

    // Verifica che l'aggiunta sia andata a buon fine
    assertNotNull(added, "Il membro aggiunto non dovrebbe essere null");
    assertEquals("Luigi Bianchi", added.getNominativo(), "Il nome del membro dovrebbe corrispondere");
    assertTrue(team.getMembers().contains(added), "Il team dovrebbe contenere il nuovo membro");

    LOGGER.info("Staff member aggiunto correttamente: " + added.getNominativo());
    }



    @Test
    @Order(2)
    void testAddMember_NullEvent_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        // Aggiunta del membro
        StaffMember added = app.getStaffManager().addNewMemberForTheEvent(staffMember, testEvent);

        assertThrows(UseCaseLogicException.class, () -> {
            app.getStaffManager().addNewMemberForTheEvent(added, null);
        }, "Should throw UseCaseLogicException if event is null");
    }

    @Test
    @Order(3)
    void testAddMember_NotOrganizer_Throws() throws UseCaseLogicException {
        // Login come utente non-organizer
        app.getUserManager().fakeLogin(nonOrganizer.getUserName());

        // Aspettati l'eccezione
        UseCaseLogicException ex = assertThrows(UseCaseLogicException.class, () -> {
            app.getStaffManager().addNewMemberForTheEvent(staffMember, testEvent);
        }, "Should throw UseCaseLogicException if user is not organizer");

        // Verifica messaggio di errore
        assertEquals("The User is not an organizer you can't add a member fot he event", ex.getMessage());
    }
}

