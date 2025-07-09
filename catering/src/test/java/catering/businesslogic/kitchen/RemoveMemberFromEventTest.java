package catering.businesslogic.kitchen;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.staff.Team;
import catering.businesslogic.user.User;
import catering.businesslogic.staff.StaffMember;
import catering.persistence.PersistenceManager;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RemoveMemberFromEventTest {

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
    void testRemoveMember_Success() throws UseCaseLogicException {
        // Login come organizer
        app.getUserManager().fakeLogin(organizer.getUserName());

        // Carica il team dall'evento (già caricato correttamente da DB)
        Team team = testEvent.getTeam();
        assertNotNull(team, "Il team dell'evento dovrebbe essere presente");

        // Aggiunge Luigi al team solo se non è già presente 
        if (team.getMembers().stream().noneMatch(m -> m.getId().equals(staffMember.getId()))) {
           
            // Aggiunge il membro Luigi Bianchi all'evento
            StaffMember added = app.getStaffManager().addNewMemberForTheEvent(staffMember, testEvent);
        }

        // Verifica che Luigi sia effettivamente nel team prima della rimozione
        assertTrue(team.getMembers().stream().anyMatch(m -> m.getId().equals(staffMember.getId())), 
            "Il team dovrebbe contenere Luigi prima della rimozione");

        // Rimuove Luigi dal team
        Boolean removed = app.getStaffManager().RemoveMemberFromEvent(team, staffMember.getId());

        // Verifica che la rimozione sia andata a buon fine
        assertTrue(removed, "Il membro dovrebbe essere rimosso con successo");

        // Verifica che Luigi non sia più nel team dopo la rimozione
        assertFalse(team.getMembers().stream().anyMatch(m -> m.getId().equals(staffMember.getId())), 
            "Il team non dovrebbe più contenere Luigi dopo la rimozione");
    }





    @Test
    @Order(2)
        void testRemoveMember_NullStaffMember_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        assertThrows(UseCaseLogicException.class, () -> {
            app.getStaffManager().RemoveMemberFromEvent(testEvent.getTeam(), null);
        });
    }   

    @Test
    @Order(3)
    void testRemoveMember_UserNotOrganizer_Throws() throws UseCaseLogicException {
        // Login come non-organizer
        app.getUserManager().fakeLogin(nonOrganizer.getUserName());

        // Ottieni il team dall'evento
        Team team = testEvent.getTeam();
        assertNotNull(team, "Il team dell'evento dovrebbe essere presente");

        // Verifica che venga sollevata l'eccezione se un non-organizer prova a rimuovere un membro
        UseCaseLogicException thrown = assertThrows(UseCaseLogicException.class, () -> {
            app.getStaffManager().RemoveMemberFromEvent(team, staffMember.getId());
        });

        assertEquals("The User is not an organizer, you can't remove a member from the event", thrown.getMessage());

    }


}
