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

    public Event CreateEvent() throws UseCaseLogicException{      
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if (u == null) {
            System.out.println("Nessun utente autenticato, operazione negata.");
            return null;
        }

        if (u.isOrganizer()) {
            Event e=CatERing.getInstance().getEventManager().createEvent(u);
                                                //SUMMARY SCHEME è UNA COSA CHE UN EVENTO PUO AVERE O NON AVERE QUIDNI QUA NON VIENE FATTO, RIMUOVERE DA DSD (ULTIMO PUNTO MESSAGGIO WHATSAPP DSD1)
            notifyEventCreated(e);
            return e;
        } else {
            throw new UseCaseLogicException("L'utente " + u.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
    }

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
