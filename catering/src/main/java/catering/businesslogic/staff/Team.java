package catering.businesslogic.staff;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import catering.businesslogic.UseCaseLogicException;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

//aggiunto 
public class Team {
    
    private static final Logger LOGGER = LogManager.getLogger(Team.class);
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


    // Database operations
    public void saveNewTeam() {
        String query = "INSERT INTO Team (staffMember_id) VALUES (?)";

        PersistenceManager.executeUpdate(query, getStaffMemberID());

        LOGGER.info("Saved Staff Member");
    }

    public ArrayList<StaffMember> getTeam() {
        return team;
    }



    public void setTeam(ArrayList<StaffMember> team) {
        this.team = team;
    }
    
    public List<Integer> getStaffMemberID(){
        ArrayList<Integer> result= new ArrayList<>();
        for (StaffMember sm: team){
            result.add(sm.getId());
        }
        return result;
    }
    
}
