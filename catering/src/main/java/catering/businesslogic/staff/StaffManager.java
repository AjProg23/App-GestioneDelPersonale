package catering.businesslogic.staff;

import java.util.ArrayList;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.Event;
import catering.businesslogic.event.EventManager;
import catering.businesslogic.event.SummaryScheme;
import catering.businesslogic.user.User;
import catering.businesslogic.user.UserManager;

//aggiunto 
public class StaffManager {
    
    private ArrayList<StaffEventReceiver> staffEventReceivers;
    private String[] eventFeatures; 
    private UserManager userManager;  
    private EventManager eventManager;
    private StaffMemberManager staffMemberManager;
    private TeamManager teamManager;

    public StaffManager(UserManager userManager,EventManager eventManager,StaffMemberManager staffMemberManager, TeamManager teamManager) {
        this.staffEventReceivers = new ArrayList<>();
        this.userManager = userManager;
        this.eventManager=eventManager;
        this.staffMemberManager=staffMemberManager;
        teamManager=teamManager;
    }

    public Event CreateEvent() throws UseCaseLogicException{      
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if (u == null) {
            System.out.println("Nessun utente autenticato, operazione negata.");
            return null;
        }

         
        if (u.isOrganizer()) {
            System.out.println("L'utente è un organizzatore, può creare l'evento.");
            //stostituire queste creazioni di StaffMember con un richiamo alla funzione create StaffMember della classe StaffMemberManager 
            ArrayList<StaffMember> staffMembers= new ArrayList();
            StaffMember st=staffMemberManager.createStaffMember();
            staffMembers.add(st);
            Team t= teamManager.createTeam(staffMembers);
            Event e=eventManager.createEvent("nome", null, null, u, t);
            SummaryScheme summaryScheme= new SummaryScheme(e, 0, null, null, null);
            notifyEventCreated(e);
            return e;
        } else {
            throw new UseCaseLogicException("L'utente " + u.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
    }

    public Event chooseEvent(Event e) throws UseCaseLogicException{
        User u = CatERing.getInstance().getUserManager().getCurrentUser();
        if(u.isOrganizer()){
            eventManager.setCurrentEvent(e);
        }
        else{
            throw new UseCaseLogicException("L'utente " + u.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
        return  e;
    }

    // Notification methods to avoid code duplication
    private void notifyEventCreated(Event event) {
        for (StaffEventReceiver staffEventReceivers : staffEventReceivers) {
            staffEventReceivers.updateEventCreated(event);
        }
    }

    

    


}
