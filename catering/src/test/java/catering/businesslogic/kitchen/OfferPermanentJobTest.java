package catering.businesslogic.kitchen;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.staff.StaffManager;
import catering.businesslogic.staff.StaffMember;
import catering.businesslogic.user.User;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OfferPermanentJobTest {

    private static CatERing app;
    private static StaffManager staffManager;
    private static User organizer;
    private static User nonOrganizer;
    private static StaffMember staffMember;

    @BeforeAll
    static void init() {
        app = CatERing.getInstance();
        staffManager = app.getStaffManager();
    }

    @BeforeEach
    void setup() {
        organizer = User.load("Francesca"); // organizer
        nonOrganizer = User.load("Antonio"); // non organizer
        staffMember = StaffMember.loadByName("Luigi Bianchi");
    }

    @Test
    @Order(1)
    void testOfferPermanentJob_Success() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        StaffMember updatedMember = staffManager.offerPermanentJob(staffMember);

        assertNotNull(updatedMember, "StaffMember aggiornato non deve essere null");
        assertTrue(updatedMember.isPermanente(), "StaffMember deve essere permanente");
    }

    @Test
    @Order(2)
    void testOfferPermanentJob_NullStaffMember_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(organizer.getUserName());

        UseCaseLogicException thrown = assertThrows(UseCaseLogicException.class, () -> {
            staffManager.offerPermanentJob(null);
        });

        assertEquals("Staff member not selected", thrown.getMessage());
    }

    @Test
    @Order(3)
    void testOfferPermanentJob_UserNotOrganizer_Throws() throws UseCaseLogicException {
        app.getUserManager().fakeLogin(nonOrganizer.getUserName());


        UseCaseLogicException thrown = assertThrows(UseCaseLogicException.class, () -> {
            staffManager.offerPermanentJob(staffMember);
        });

        assertEquals("The User is not an organizer you can't offer a permanent job", thrown.getMessage());
    }
}
