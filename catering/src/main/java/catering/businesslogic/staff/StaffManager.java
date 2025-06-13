package catering.businesslogic.staff;

import java.util.ArrayList;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.event.SummaryScheme;
import catering.businesslogic.user.User;


//aggiunto 
public class StaffManager {
    
    private ArrayList<StaffEventReceiver> staffEventReceivers;
    private String[] eventFeatures; 
    

    public StaffManager() {
        this.staffEventReceivers = new ArrayList<>();
    }

    /**
     * create an event
     * @throws UseCaseLogicException            if the user is not an organizer
     */
    public Event CreateEvent() throws UseCaseLogicException{      
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if (u == null) {
            System.out.println("Nessun utente autenticato, operazione negata.");
            return null;
        }
        if (u.isOrganizer()) {
            Event e=CatERing.getInstance().getEventManager().createEvent(u);
            notifyEventCreated(e);
            return e;
        } else {
            throw new UseCaseLogicException("L'utente " + u.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
    }

    
    /**
     * select an event 
     * @param e  the event the client want to select
     * @throws UseCaseLogicException            if the user is not an organizer
     */
    public Event chooseEvent(Event e) throws UseCaseLogicException{
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if(u.isOrganizer()){
        CatERing.getInstance().getEventManager().setCurrentEvent(e);
        }
        else{
            throw new UseCaseLogicException("L'utente " + u.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
        return  e;
    }

    // Notification methods to avoid code duplication
    private void notifyEventCreated(Event event) {
        for (StaffEventReceiver receiver : staffEventReceivers) {
            receiver.updateEventCreated(event);
        }
    }

    
    /**
     * Create a summare scheme to associate top the current event
     * 
     * @param nrOfStaffMembersRequired          The number of staff reqwuired for the event 
     * @param transportationNeeds               The Trasportqation nedded fot the event
     * @param typeOfService                     The Type of service
     * @param clientRequest                     the specific request from the client
     * @throws UseCaseLogicException            if no event is selected
     */
    public SummaryScheme creatSummaryScheme(int nrOfStaffMembersRequired, String transportationNeeds, String typeOfService, String clientRequest) throws UseCaseLogicException{
        Event currEvent= CatERing.getInstance().getEventManager().getCurrentEvent();
        if(currEvent==null){
            throw new UseCaseLogicException("Nessun evento corrente di cui creare il summary scheme");
        }
        SummaryScheme ss=CatERing.getInstance().getEventManager().addSummaryScheme();
        notifySummarySchemeCreated(ss);
        return ss;
    }
    private void notifySummarySchemeCreated(SummaryScheme ss) {
        for(StaffEventReceiver receiver: staffEventReceivers){
            receiver.updateSummarySchemeCreated(ss);
        }
    }

    /**
     * Add a new member for the team of the current event
     * @throws UseCaseLogicException            if no event is selected
     *  @throws UseCaseLogicException            if the user is not an organizer
     */
    public StaffMember addNewMemberForTheEvent()throws UseCaseLogicException{
        Event currEvent= CatERing.getInstance().getEventManager().getCurrentEvent();
        User u=CatERing.getInstance().getUserManager().getCurrentUser();
        if(currEvent==null){
            throw new UseCaseLogicException("Nessun evento selezionato per aggiungere componente al team");
        }
        if (!u.isOrganizer()) {
            throw new UseCaseLogicException("The User is not an organizer you can't add a member fot he event");
        }
        StaffMember member = CatERing.getInstance().getStaffMemberManager().addNewMemberForTheEvent(currEvent);
        return member;
    }

    public ArrayList<StaffEventReceiver> getStaffEventReceivers() {
        return staffEventReceivers;
    }

    public void setStaffEventReceivers(ArrayList<StaffEventReceiver> staffEventReceivers) {
        this.staffEventReceivers = staffEventReceivers;
    }

    public String[] getEventFeatures() {
        return eventFeatures;
    }

    public void setEventFeatures(String[] eventFeatures) {
        this.eventFeatures = eventFeatures;
    }
    

    


}
