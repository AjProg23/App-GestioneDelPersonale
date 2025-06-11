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

    private void notifyTeamCreated(Team t) {
       for (TeamReceiver tr : this.TeamReceivers) {
            tr.updateTeamCreated(t);
        }
    }
    
}
