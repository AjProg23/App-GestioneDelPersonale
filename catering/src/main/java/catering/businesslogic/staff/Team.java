package catering.businesslogic.staff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import catering.businesslogic.UseCaseLogicException;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

//aggiunto 
public class Team {
    
    private static final Logger LOGGER = LogManager.getLogger(Team.class);
    private List<StaffMember> team;
    public Team() {
    }

    public Team(ArrayList<StaffMember> team) {
        this.team = team;
    }


    /**
     * Add a new member for this team 
     * @throws UseCaseLogicException            if no event is selected
     */
    public Boolean addMember(StaffMember member) {
        if (member != null) {
            team.add(member);
            System.out.println("Aggiunto al team: " + member.getNominativo());
        }
        return true;
    }


    /**
     * Remove a staff member from the team team 
     * @param memberID                          the id of the staff member the user want to remove
     * @return TRUE if it is pobble to remove the staff member, false otherwise
     */
    public Boolean removeMember(Integer memberID) {
    Iterator<StaffMember> iterator = team.iterator();
    while (iterator.hasNext()) {
        StaffMember member = iterator.next();
        if (member.getId().equals(memberID)) {
            iterator.remove();
            removeStaffMember(memberID);
            return true;
        }
    }
    LOGGER.info("Impossible to remove the staff member, it is not a member of the team");
    return false;
}

    // Database operations
    public void saveNewTeam() {
        String query = "INSERT INTO Team (staffMember_id) VALUES (?)";

        PersistenceManager.executeUpdate(query, getStaffMemberID());

        LOGGER.info("Saved Staff Member");
    }


    public void removeStaffMember(Integer memberID){
        String query = "DELETE FROM Team WHERE staffMember_id = ?";
        PersistenceManager.executeUpdate(query, memberID);

        LOGGER.info("Removed Staff Member with ID: " + memberID);
    }




    public List<StaffMember> getTeam() {
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
