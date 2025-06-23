package catering.businesslogic.staff;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import catering.businesslogic.event.Vacation;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

public class StaffMember {
    
    private static final Logger LOGGER = LogManager.getLogger(StaffMember.class);
    
    private int id;
    private String nominativo;
    private ArrayList<String>[] Ruoli;
    private Boolean permanente;
    private List<Vacation> vacations;
   
    
    

    public StaffMember(int id, String nominativo, ArrayList<String>[] ruoli, Boolean permanente,
            List<Vacation> vacations) {
        this.id = id;
        this.nominativo = nominativo;
        Ruoli = ruoli;
        this.permanente = permanente;
        this.vacations = vacations;
    }

    public StaffMember(int id, String nominativo, ArrayList<String>[] ruoli, Boolean permanente) {
        this.id = id;
        this.nominativo = nominativo;
        Ruoli = ruoli;
        this.permanente = permanente;
    }

    public Boolean getPermanente() {
        return permanente;
    }

    public void setPermanente(Boolean permanente) {
        this.permanente = permanente;
    }
    
   
   
    public int getId() {
        return id;
    }

    

  public void setId(int id) {
        this.id = id;
    }

    public String getNominativo() {
        return nominativo;
    }

    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    public ArrayList<String>[] getRuoli() {
        return Ruoli;
    }

    public void setRuoli(ArrayList<String>[] ruoli) {
        Ruoli = ruoli;
    }    

   public List<Vacation> getVacations() {
        return vacations;
    }

    public List<Integer> getVacationsID() {
    List<Integer> vacationsID = new ArrayList<>();
    for (Vacation v : vacations) {
        vacationsID.add(v.getId());
    }
    return vacationsID;
}


    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
    }

    // Database operations
    public void saveNewStaffMember() {
        String query = "INSERT INTO StaffMember (nominativo, ruoli, permanente, vacations_id) VALUES (?, ?, ?, ?,?,?)";


        PersistenceManager.executeUpdate(query, nominativo, Ruoli, permanente,getVacationsID());

        // Get the ID of the newly inserted event
        id = PersistenceManager.getLastId();

        LOGGER.info("Saved Staff Member");
    }

    
    
}
