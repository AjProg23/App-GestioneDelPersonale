package catering.businesslogic.staff;

import java.util.ArrayList;

import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.event.EventManager;
import catering.businesslogic.event.StaffMember;
import catering.businesslogic.event.SummaryScheme;
import catering.businesslogic.event.Team;
import catering.businesslogic.user.User;
import catering.businesslogic.user.UserManager;

//aggiunto 
public class StaffManager {
    
    private ArrayList<StaffEventReceiver> staffEventReceivers;
    private String[] eventFeatures; 
    private UserManager userManager;  

    public StaffManager(UserManager userManager) {
        this.staffEventReceivers = new ArrayList<>();
        this.userManager = userManager;
    }

    public Event CreateEvent() throws UseCaseLogicException{      
         User u = userManager.getCurrentUser(); 
        if (u == null) {
            System.out.println("Nessun utente autenticato, operazione negata.");
            return null;
        }

        boolean isOrganizer = u.getRoles().contains(User.Role.ORGANIZZATORE);  
        if (isOrganizer) {
            System.out.println("L'utente è un organizzatore, può creare l'evento.");
            EventManager Em=new EventManager();
            StaffMember SM1=new StaffMember(0, "Mario Rossi", eventFeatures, null);
            StaffMember SM2= new StaffMember(0, "Luca Pierpaolo", eventFeatures, null);
            Team team= new Team();
            team.addMember(SM1);
            team.addMember(SM2);
            Event e=Em.createEvent(null, null, null, u, team);
            SummaryScheme summaryScheme= new SummaryScheme(e, 0, null, null, null);
            notifyEventCreated(e);
            return e;
        } else {
            throw new UseCaseLogicException("L'utente " + u.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
    }


    // Notification methods to avoid code duplication

    private void notifyEventCreated(Event event) {
        for (StaffEventReceiver staffEventReceivers : staffEventReceivers) {
            staffEventReceivers.updateEventCreated(event);
        }
    }

    

    


}
