package catering.businesslogic.kitchen;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.staff.StaffManager;
import catering.businesslogic.staff.Team;
import catering.businesslogic.user.User;
import catering.businesslogic.staff.StaffMember;
import catering.persistence.PersistenceManager;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RemoveMemberFromEventTest {

    private static CatERing app;
    private static User organizer;
    private static User nonOrganizer;
    private static Team testTeam;
    private static StaffManager staffManager;

    @BeforeAll
    static void init() {
        PersistenceManager.initializeDatabase("database/catering_init_sqlite.sql");
        app = CatERing.getInstance();
        staffManager = app.getStaffManager();
    }

    @BeforeEach
    void setup() {
        organizer = User.load("Francesca"); // organizer
        nonOrganizer = User.load("Antonio"); // non organizer

        testTeam = new Team();
        // Aggiungo un membro con id 1 (fittizio, puoi adattare)
        StaffMember member = new StaffMember(1, "Test Member", null, false);
        testTeam.addMember(member);
    }

    @Test
    @Order(1)
    void testRemoveMember_Success() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        Boolean removed = staffManager.RemoveMemberFromEvent(testTeam, 1);

        assertTrue(removed, "Il membro dovrebbe essere rimosso con successo");
        assertFalse(testTeam.getMembers().stream().anyMatch(m -> m.getId() == 1), "Il membro non deve più essere nel team");
    }

    @Test
    @Order(2)
    void testRemoveMember_NullStaffMember_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        UseCaseLogicException thrown = assertThrows(UseCaseLogicException.class, () -> {
            staffManager.RemoveMemberFromEvent(testTeam, null);
        });

        assertEquals("there isn't a staff member selected", thrown.getMessage());
    }

    @Test
    @Order(3)
    void testRemoveMember_UserNotOrganizer_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(nonOrganizer.getUserName());

        UseCaseLogicException thrown = assertThrows(UseCaseLogicException.class, () -> {
            staffManager.RemoveMemberFromEvent(testTeam, 1);
        });

        assertEquals("The User is not an organizer you can't visualize the vacation request of the staff member", thrown.getMessage());
    }
}
