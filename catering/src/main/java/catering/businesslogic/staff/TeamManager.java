package catering.businesslogic.staff;

import java.util.ArrayList;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.user.User;

public class TeamManager {
    private Team currentTeam;
    private ArrayList<TeamReceiver> TeamReceivers;
    public TeamManager() {
    }

    /**
     * Create a Team
     * 
     * @param staffMembers          All the Staff Members of the team                
     * @throws UseCaseLogicException            if the User is not an organizer
     */
    public Team createTeam(ArrayList<StaffMember> staffMembers) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isOrganizer()){
            throw new UseCaseLogicException("L'utente " + user.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
        Team t= new Team (staffMembers);
        this.setCurrentTeam(t);
        this.notifyTeamCreated(t);
        return t;
    }

    private void setCurrentTeam(Team t) {
        this.currentTeam=t;
    }
    public Team getCurrentTeam() {
        return currentTeam;
    }


    /**
     * Notify the TeamReceivers that a Team had been created
     * 
     * @param t          The team created
     */
    private void notifyTeamCreated(Team t) {
       for (TeamReceiver tr : this.TeamReceivers) {
            tr.updateTeamCreated(t);
        }
    }

    
    
}
