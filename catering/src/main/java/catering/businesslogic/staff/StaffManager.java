package catering.businesslogic.staff;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.user.User;
import catering.util.LogManager;


//aggiunto 
public class StaffManager {
    private static final Logger LOGGER = LogManager.getLogger(StaffManager.class);
    private ArrayList<StaffEventReceiver> staffEventReceivers;
    private StaffMember currentStaffMember;
    private Team currentTeam;
    

    
    public StaffManager() {
        this.staffEventReceivers = new ArrayList<>();
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
        try {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isOrganizer()){
            throw new UseCaseLogicException("L'utente " + user.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
        LOGGER.info("Creating new staff member");
        StaffMember sm=new StaffMember(id, nominativo, ruoli,permanente);
        this.setCurrentStaffMember(sm);
        this.notifyStaffMemberCreated(sm);
        sm.saveNewStaffMember();
        return sm;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create staff memeber '" + nominativo+"  Errore:"+e);
            return null;
        }
    }
    

    /**
     * Create a Team
     * 
     * @param staffMembers          All the Staff Members of the team                
     * @throws UseCaseLogicException            if the User is not an organizer
     */
    public Team createTeam(ArrayList<StaffMember> staffMembers) throws UseCaseLogicException{
        try {
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isOrganizer()){
            throw new UseCaseLogicException("L'utente " + user.getUserName() + " non è un organizzatore. Creazione team negata.");
        }
        LOGGER.info("Creating new team");
        Team t= new Team (staffMembers);
        this.setCurrentTeam(t);
        this.notifyTeamCreated(t);
        t.saveNewTeam();
        return t;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create the Team for Errore:"+e);
            return null;
        }
    }

    /**
     * Add a new member for the team of the current event
     * @param member                            Member added in the team of the event
     * @param event                             the event selected for the operation
     * @throws UseCaseLogicException            if no event is selected
     * @throws UseCaseLogicException            if the user is not an organizer
     */
    public StaffMember addNewMemberForTheEvent(StaffMember member, Event event)throws UseCaseLogicException{
        User u=CatERing.getInstance().getUserManager().getCurrentUser();
        if(event==null){
            throw new UseCaseLogicException("Nessun evento selezionato per aggiungere componente al team");
        }
        if (!u.isOrganizer()) {
            throw new UseCaseLogicException("The User is not an organizer you can't add a member fot he event");
        }
        try {
            LOGGER.info("Adding the staff member "+member.getNominativo()+ " to te event "+event.getName());
            event.getTeam().addMember(member);
            this.setCurrentStaffMember(member);
            this.notifyStaffMemberAdded(member);
            member.saveNewStaffMember();
            return member;
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Failed to add the memeber for the event '" + event.getName()+".  Errore:"+exception);
            return null;
        }
    }
    

    /**
     * Offer a permanent job for the Staff Member
     * @param  sm the staff member selected 
     * @return the staffMember for the permanent job
     * @throws UseCaseLogicException            if there isn't a staff member selected
     * @throws UseCaseLogicException            if the user is not an organizer
     */
    public StaffMember offerPermanentJob(StaffMember sm) throws UseCaseLogicException{
        User u=CatERing.getInstance().getUserManager().getCurrentUser();
        if (!u.isOrganizer()) {
            throw new UseCaseLogicException("The User is not an organizer you can't offer a permanent job");
        }
        if(sm==null){
            throw new UseCaseLogicException("Staff member not selected");
        }
        try {
            LOGGER.info("Offering permanent job to staff member "+sm.getNominativo());
        sm.setPermanente(true);
        this.notifyStaffMemberPermanentJob(sm);
        this.setCurrentStaffMember(sm);
        sm.udpatePermanentJob();
        return sm;
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Failed to register as permanent for the staff member" + sm.getNominativo()+".  Errore:"+exception);
            return null;
        }
    }

    /**
     * Get the vacation requests from the specific staff member
     * @param  sm the staff member selected 
     * @throws UseCaseLogicException            if there isn't a staff member selected
     * @throws UseCaseLogicException            if the user is not an organizer
     * @return the vacations of the staff member
     */
    public List<Vacation> getVacationRequest(StaffMember sm)throws UseCaseLogicException{
        User u=CatERing.getInstance().getUserManager().getCurrentUser();
        if (!u.isOrganizer()) {
            throw new UseCaseLogicException("The User is not an organizer you can't visualize the vacation request of the staff member");
        }
        if(sm==null){
            throw new UseCaseLogicException("Staff member not selected");
        }
        List<Vacation> vacations=sm.getVacations();
        return vacations;
    }


        

    /**
     * Notify the StaffEventReceiver that a StaffMember had been created
     * 
     * @param sm          The staff member created
     */
    private void notifyStaffMemberCreated(StaffMember sm) {
        for (StaffEventReceiver ser : this.staffEventReceivers) {
            ser.updateStaffMemberCreated(sm);
        }
    }


    /**
     * Notify the StaffEventReceiver that a StaffMember had been added to the team of the currentEvent
     * 
     * @param sm          The staff member added to the current event
     */
    private void notifyStaffMemberAdded(StaffMember sm) {
        for (StaffEventReceiver ser : this.staffEventReceivers) {
            ser.updateStaffMemberAdded(sm);
        }
    }

    /**
     * Notify the StaffEventReceiver that a StaffMember took a permanent job
     * 
     * @param sm          The staff member for the permanent job
     */
    private void notifyStaffMemberPermanentJob(StaffMember sm){
        for (StaffEventReceiver ser : this.staffEventReceivers) {
            ser.UpdateStaffMemberPermanentJob(sm);
        }
    }


    /**
     * Add a new evevent receiver 
     *  
     */
    public void addReceiver(StaffEventReceiver ser){
        staffEventReceivers.add(ser);
    }

    /**
     * Remove a staffEventReceiver from the staffEventReceivers 
     *  
     */
    public void removeReceiver(StaffEventReceiver ser){
        staffEventReceivers.remove(ser);
    }

    

    /**
     * Notify the TeamReceivers that a Team had been created
     * 
     * @param t          The team created
     */
    private void notifyTeamCreated(Team t) {
       for (StaffEventReceiver ser : this.staffEventReceivers) {
            ser.updateTeamCreated(t);
        }
    }

    public ArrayList<StaffEventReceiver> getStaffEventReceivers() {
        return staffEventReceivers;
    }

    public void setStaffEventReceivers(ArrayList<StaffEventReceiver> staffEventReceivers) {
        this.staffEventReceivers = staffEventReceivers;
    }

    
    private void setCurrentStaffMember(StaffMember sm) {
        this.currentStaffMember=sm;
    }

    public StaffMember getCurrentStaffMember() {
        return currentStaffMember;
    }
    
    private void setCurrentTeam(Team t) {
        this.currentTeam=t;
    }
    public Team getCurrentTeam() {
        return currentTeam;
    }

}
