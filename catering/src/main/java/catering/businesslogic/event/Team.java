package catering.businesslogic.event;

import java.util.Set;

//aggiunto 
public class Team {
    private Set<StaffMember> team;

    
    public Team() {
    }

    public Team(Set<StaffMember> team) {
        this.team = team;
    }

    public Set<StaffMember> getTeam() {
        return team;
    }

    public void setTeam(Set<StaffMember> team) {
        this.team = team;
    }
    
    public void addMember(StaffMember member) {
        if (member != null) {
            team.add(member);
            System.out.println("Aggiunto al team: " + member.getNominativo());
        }
    }
    
}
