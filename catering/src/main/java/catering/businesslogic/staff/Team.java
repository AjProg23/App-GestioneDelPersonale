package catering.businesslogic.staff;

import java.util.ArrayList;

import catering.businesslogic.UseCaseLogicException;

//aggiunto 
public class Team {
    private ArrayList<StaffMember> team;
    public Team() {
    }

    
    
    public Team(ArrayList<StaffMember> team) {
        this.team = team;
    }


    /**
     * Add a new member for this team 
     * @throws UseCaseLogicException            if no event is selected
     */
    public void addMember(StaffMember member) {
        if (member != null) {
            team.add(member);
            System.out.println("Aggiunto al team: " + member.getNominativo());
        }
    }



    public ArrayList<StaffMember> getTeam() {
        return team;
    }



    public void setTeam(ArrayList<StaffMember> team) {
        this.team = team;
    }
    
}
