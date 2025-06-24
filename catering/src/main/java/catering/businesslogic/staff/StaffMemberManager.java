package catering.businesslogic.staff;

import java.util.ArrayList;
import java.util.List;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.event.Vacation;
import catering.businesslogic.user.User;

public class StaffMemberManager {
    private StaffMember currentStaffMember;
    private ArrayList<StaffMemberReceiver> staffMemberReceivers;

    public StaffMemberManager() {
    }

    private void setCurrentStaffMember(StaffMember sm) {
        this.currentStaffMember=sm;
    }

    public StaffMember getCurrentStaffMember() {
        return currentStaffMember;
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
    public StaffMember createStaffMember(int id, String nominativo, ArrayList<String>[] ruoli, Boolean permanente) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isOrganizer()){
            throw new UseCaseLogicException("L'utente " + user.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
        StaffMember sm=new StaffMember(id, nominativo, ruoli,permanente);
        this.setCurrentStaffMember(sm);
        this.notifyStaffMemberCreated(sm);
        sm.saveNewStaffMember();
        return sm;
    }
    public StaffMember createStaffMember() throws UseCaseLogicException{
        return this.createStaffMember(0, "Mario Rossi",null, true);
    } 

    

    /**
     * Add a new member for the team of the current event
     * @param  e the event selected 
     * @return the staffMember added for the event
     */
    public StaffMember addNewMemberForTheEvent(Event e, StaffMember sm )throws UseCaseLogicException{
        Event currEvent= CatERing.getInstance().getEventManager().getCurrentEvent();
        currEvent.getTeam().addMember(sm);
        sm.saveNewStaffMember();
        notifyStaffMemberAdded(sm);
        return sm;
    }

    /**
     * Offet a permanent job for the Staff Member
     * @param  sm the staff member selected 
     * @return the staffMember for the permanent job
     */
    public StaffMember offerPermanentJob(StaffMember sm) throws UseCaseLogicException{
        sm.setPermanente(true);
        sm.udpatePermanentJob();
        notifyStaffMemberPermanentJob(sm);
        return sm;
    }

    /**
     * Get the vacation requests from the specific staff member
     * @param  sm the staff member selected 
     * @return the vacations of the staff member
     */
    public List<Vacation> getVacationRequest(StaffMember sm){
        List<Vacation> vacations=sm.getVacations();
        return vacations;
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
     * Notify the StaffMemberReceiver that a StaffMember had been added to the team of the currentEvent
     * 
     * @param sm          The staff member added to the current event
     */
    private void notifyStaffMemberAdded(StaffMember sm) {
        for (StaffMemberReceiver smr : this.staffMemberReceivers) {
            smr.updateStaffMemberAdded(sm);
        }
    }

    /**
     * Notify the StaffMemberReceiver that a StaffMember took a permanent job
     * 
     * @param sm          The staff member for the permanent job
     */
    private void notifyStaffMemberPermanentJob(StaffMember sm){
        for (StaffMemberReceiver smr : this.staffMemberReceivers) {
            smr.UpdateStaffMemberPermanentJob(sm);
        }
    }


    
}
