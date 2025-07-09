package catering.businesslogic.staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import catering.businesslogic.UseCaseLogicException;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

public class StaffMember {
    private static final Logger LOGGER = LogManager.getLogger(StaffMember.class);

    private Integer id;
    private String nominativo;
    private List<String> ruoli = new ArrayList<>();
    private Boolean permanente;
    private List<Vacation> vacations;                            //not in the DB, vacation has the refeer to the staff member

    public StaffMember(Integer id, String nominativo, List<String> ruoli, Boolean permanente) {
        this.id = id;
        this.nominativo = nominativo;
        this.ruoli = ruoli;
        this.permanente = permanente;
        this.vacations=new ArrayList<>();
    }

    /**
     * Add a new Vacation request for this staffMember
     * @param  v                    the vacation request
     */
    public void addVacations(Vacation v){
        vacations.add(v);
    }


    //DB FUNCTIONES
    /**
     * INSERT in StaffMember
     *
     */
    public void saveNewStaffMember() {
        String insertSql = "INSERT INTO StaffMember (nominativo, ruoli, permanente) VALUES (?, ?, ?)";
        PersistenceManager.executeUpdate(insertSql,
                                         nominativo,
                                         toStringRuoli(),
                                         permanente);
        this.id = PersistenceManager.getLastId();
        LOGGER.info("Nuovo StaffMember creato con ID=" + id);
        // qui si potrebbero gestire vacanze, ruoli separati, ecc.
    }

    /**
     * UPDATE dei dati base dello staff member.
     */
    public void updateStaffMember() {
        if (id == null) {
            LOGGER.warning("updateStaffMember: id non valorizzato");
            return;
        }
        String updateSql = "UPDATE StaffMember "
                         + "SET nominativo = ?, ruoli = ?, permanente = ? "
                         + "WHERE id = ?";
        PersistenceManager.executeUpdate(updateSql,
                                         nominativo,
                                         toStringRuoli(),
                                         permanente,
                                         id);
        LOGGER.info("StaffMember aggiornato: ID=" + id);
    }



    public List<Vacation> getVacations() {
        return vacations;
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
    }

    public StaffMember(String nominativo, List<String> ruoli, Boolean permanente) {
        this(null, nominativo, ruoli, permanente);
    }

    public Integer getId() {
        return id;
    }

    public String getNominativo() {
        return nominativo;
    }

    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    public List<String> getRuoli() {
        return ruoli;
    }

    public void setRuoli(List<String> ruoli) {
        this.ruoli = ruoli;
    }

    public Boolean isPermanente() {
        return permanente;
    }

    public void setPermanente(Boolean permanente) {
        this.permanente = permanente;
    }

    /**
     * Serializza i ruoli in un'unica stringa separata da virgole.
     */
    private String toStringRuoli() {
        return String.join(", ", ruoli);
    }

    public static StaffMember loadByName(String name) {
    StaffMember sm = null;

    String query = "SELECT * FROM StaffMember WHERE nominativo = ?";

    try (Connection conn = PersistenceManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Integer id = rs.getInt("id");
            String nominativo = rs.getString("nominativo");
            String ruoliStr = rs.getString("ruoli");
            Boolean permanente = rs.getBoolean("permanente");

            // Split dei ruoli su virgola e trim degli spazi
            List<String> ruoli = Arrays.stream(ruoliStr.split(","))
                                       .map(String::trim)
                                       .collect(Collectors.toList());

            sm = new StaffMember(id, nominativo, ruoli, permanente);
        }

        rs.close();
    } catch (SQLException e) {
        e.printStackTrace(); // o usa un logger se preferisci
    }

    return sm;
    }

    public void loadVacations() throws UseCaseLogicException {
        this.vacations = Vacation.loadByStaffMemberId(this.id);
    }

    public static List<StaffMember> loadByTeamId(int teamId) {
    List<StaffMember> members = new ArrayList<>();

    String query = "SELECT sm.id, sm.nominativo, sm.ruoli, sm.permanente " +
                   "FROM StaffMember sm " +
                   "JOIN Team_StaffMember tsm ON sm.id = tsm.staff_member_id " +
                   "WHERE tsm.team_id = ?";

    PersistenceManager.executeQuery(query, rs -> {
        int id = rs.getInt("id");
        String nominativo = rs.getString("nominativo");
        String ruoliStr = rs.getString("ruoli");
        boolean permanente = rs.getBoolean("permanente");

        List<String> ruoli = new ArrayList<>();
        if (ruoliStr != null && !ruoliStr.isEmpty()) {
            ruoli = Arrays.asList(ruoliStr.split(",\\s*"));
        }

        StaffMember sm = new StaffMember(id, nominativo, ruoli, permanente);
        members.add(sm);
    }, teamId);

    return members;
 }

}
