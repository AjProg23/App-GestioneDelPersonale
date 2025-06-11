package catering.businesslogic.staff;

import java.util.ArrayList;

//aggiunto 
public class Team {
    private ArrayList<StaffMember> team;

    
    public Team() {
    }

    
    
    public Team(ArrayList<StaffMember> team) {
        this.team = team;
    }



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
