package catering.businesslogic.staff;

import java.util.ArrayList;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.staff.StaffMember.Role;
import catering.businesslogic.user.User;

public class StaffMemberManager {
    private StaffMember currentStaffMember;
    private ArrayList<StaffMemberReceiver> staffMemberReceivers;

    public StaffMemberManager() {
    }

    

    /**
     * Create a Staff member
     * 
     * @param id          The identifier for the member
     * @param nominativo               The name of the staff member
     * @param features                     
     * @param featuresValue                     
     * @throws UseCaseLogicException            if no event is selected
     */
    public StaffMember createStaffMember(int id, String nominativo, ArrayList<Role>[] ruoli, Boolean permanente) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isOrganizer()){
            throw new UseCaseLogicException("L'utente " + user.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
        StaffMember sm=new StaffMember(id, nominativo, ruoli,permanente);
        this.setCurrentStaffMember(sm);
        this.notifyStaffMemberCreated(sm);
        return sm;
    }
    public StaffMember createStaffMember() throws UseCaseLogicException{
        return this.createStaffMember(0, "Mario Rossi",null, true);
    } 

    private void setCurrentStaffMember(StaffMember sm) {
        this.currentStaffMember=sm;
    }

    public StaffMember getCurrentStaffMember() {
        return currentStaffMember;
    }

    /**
     * Add a new member for the team of the current event
     * @param  e the event selected 
     */
    public StaffMember addNewMemberForTheEvent(Event e)throws UseCaseLogicException{
        Event currEvent= CatERing.getInstance().getEventManager().getCurrentEvent();
        StaffMember sm= createStaffMember();
        currEvent.getTeam().addMember(sm);
        notifyStaffMemberAdded(sm);
        return sm;
    }

    
    /**
     * Notify the StaffMemberReceiver that a StaffMember had been reated
     * 
     * @param sm          The staff member created
     */
    private void notifyStaffMemberCreated(StaffMember sm) {
        for (StaffMemberReceiver smr : this.staffMemberReceivers) {
            smr.updateStaffMemberCreated(sm);
        }
    }


    /**
     * Notify the StaffMemberReceiver that a StaffMember had been added to the teamof the currentEvent
     * 
     * @param sm          The staff member added to the current event
     */
    private void notifyStaffMemberAdded(StaffMember sm) {
        for (StaffMemberReceiver smr : this.staffMemberReceivers) {
            smr.updateStaffMemberAdded(sm);
        }
    }


    
}
