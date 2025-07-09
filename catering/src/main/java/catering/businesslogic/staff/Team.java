package catering.businesslogic.staff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import catering.persistence.PersistenceManager;
import catering.util.LogManager;

public class Team {
    private static final Logger LOGGER = LogManager.getLogger(Team.class);

    private Integer id;
    private List<StaffMember> members = new ArrayList<>();
    private boolean isBeingModified;

    public Team() { }


    public Team(List<StaffMember> members) {
        this.members = members;
    }

    public Integer getId() {
        return id;
    }

    public List<StaffMember> getMembers() {
        return members;
    }

    public void setMembers(List<StaffMember> members) {
        this.members = members;
    }

    /**
     * Inserisce la riga in Team e nelle righe di join Team_StaffMember.
     */
    public void saveNewTeam() {
        // 1. Creo il record in Team
        String insertTeam = "INSERT INTO Team DEFAULT VALUES";
        PersistenceManager.executeUpdate(insertTeam);
        this.id = PersistenceManager.getLastId();

        // 2. Creo le righe di join
        String linkSql = "INSERT INTO Team_StaffMember (team_id, staff_member_id) VALUES (?, ?)";
        for (StaffMember sm : members) {
            PersistenceManager.executeUpdate(linkSql, id, sm.getId());
        }
        LOGGER.info("Team creato con ID=" + id + " e " + members.size() + " membri.");
    }

    public boolean addMember(StaffMember member) {
    if (member == null) return false;

    // Rimuovi dal DB se esiste già (per evitare violazione UNIQUE)
    if (id != null) {
        String deleteSql = "DELETE FROM Team_StaffMember WHERE team_id = ? AND staff_member_id = ?";
        PersistenceManager.executeUpdate(deleteSql, id, member.getId());

        String insertSql = "INSERT INTO Team_StaffMember (team_id, staff_member_id) VALUES (?, ?)";
        PersistenceManager.executeUpdate(insertSql, id, member.getId());
        LOGGER.info("Aggiunto al team (DB) staffMember_id=" + member.getId());
    }

    // Aggiungi in memoria solo se non presente
    if (!members.contains(member)) {
        members.add(member);
        LOGGER.info("Aggiunto in memoria al team: " + member.getNominativo());
    }

    return true;
    }



    /**
     * Rimuove un membro dal team: in memoria e nella tabella di join.
     */
    public boolean removeMember(Integer memberId) {
        Iterator<StaffMember> it = members.iterator();
        while (it.hasNext()) {
            StaffMember sm = it.next();
            if (sm.getId().equals(memberId)) {
                it.remove();
                if (id != null) {
                    String deleteLink = "DELETE FROM Team_StaffMember "
                                      + "WHERE team_id = ? AND staff_member_id = ?";
                    PersistenceManager.executeUpdate(deleteLink, id, memberId);
                    LOGGER.info("Rimosso dal DB staffMember_id=" + memberId);
                }
                LOGGER.info("Rimosso in memoria staffMember_id=" + memberId);
                return true;
            }
        }
        LOGGER.warning("Impossibile rimuovere: staffMember_id=" + memberId + " non presente.");
        return false;
    }


    public void setId(int i) {
        this.id=i;
    }


    public boolean isBeingModified() {
    // qui la logica che ritorna true se il team è in modifica 
    return this.isBeingModified; // esempio
}


    public static Team loadById(int teamId) {
    final Team[] teamHolder = new Team[1];
    final boolean[] found = new boolean[1];

    String query = "SELECT * FROM Team WHERE id = ?";

    PersistenceManager.executeQuery(query, rs -> {
        found[0] = true;
        Team t = new Team();
        t.id = rs.getInt("id");

        // Carichiamo i membri del team
        t.members = StaffMember.loadByTeamId(t.id);

        teamHolder[0] = t;
    }, teamId);

    if (!found[0]) {
        return null;
    }
    return teamHolder[0];
}


}
