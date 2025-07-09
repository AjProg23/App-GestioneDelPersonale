package catering.businesslogic.kitchen;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.staff.StaffManager;
import catering.businesslogic.staff.StaffMember;
import catering.businesslogic.user.User;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetVacationRequestTest {

    private static final Logger LOGGER = LogManager.getLogger(GetVacationRequestTest.class);

    private static CatERing app;
    private static User organizer;
    private static User nonOrganizer;
    private static StaffManager staffManager;
    private static StaffMember staffMember;

    @BeforeAll
    static void init() {
        PersistenceManager.initializeDatabase("database/catering_init_sqlite.sql");
        app = CatERing.getInstance();
        staffManager = app.getStaffManager();
    }

    @BeforeEach
    void setup() throws UseCaseLogicException {
        organizer = User.load("Francesca"); // organizer
        nonOrganizer = User.load("Antonio"); // non-organizer
        staffMember = StaffMember.loadByName("Luigi Bianchi");
        staffMember.loadVacations();

    }

    @Test
    @Order(1)
    void testGetVacation_Success() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        List<catering.businesslogic.staff.Vacation> vacations =
                staffManager.getVacationRequest(staffMember);

        assertNotNull(vacations, "La lista di vacanze non dovrebbe essere nulla");
        assertTrue(vacations.size() >= 0, "La lista vacanze dovrebbe essere >= 0");

        LOGGER.info("Vacanze trovate per Luigi: " + vacations.size());
    }

    @Test
    @Order(2)
    void testGetVacation_StaffMemberNull_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        UseCaseLogicException ex = assertThrows(UseCaseLogicException.class, () -> {
            staffManager.getVacationRequest(null);
        });

        assertEquals("Staff member not selected", ex.getMessage());
    }

    @Test
    @Order(3)
    void testGetVacation_UserNotOrganizer_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(nonOrganizer.getUserName());

        UseCaseLogicException ex = assertThrows(UseCaseLogicException.class, () -> {
            staffManager.getVacationRequest(staffMember);
        });

        assertEquals("The User is not an organizer you can't visualize the vacation request of the staff member",
                     ex.getMessage());
    }
}
