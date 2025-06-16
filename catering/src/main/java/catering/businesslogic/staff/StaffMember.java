package catering.businesslogic.staff;

import java.util.ArrayList;
import java.util.List;

import catering.businesslogic.event.Vacation;

public class StaffMember {
    
    public static enum Role {
        Pasticciere, Barista, Magazziniere, Cameriere
    };
    
    private int id;
    private String nominativo;
    private ArrayList<Role>[] Ruoli;
    private Boolean permanente;
    private List<Vacation> vacations;
   
    public StaffMember(int id, String nominativo, ArrayList<Role>[] Ruoli,  Boolean permanente) {
        this.Ruoli = Ruoli;
        this.id = id;
        this.nominativo = nominativo;
        this.permanente = permanente;
    }

    public StaffMember(ArrayList<Role>[] Ruoli, int id, String nominativo, Boolean permanente, List<Vacation> vacations) {
        this.Ruoli = Ruoli;
        this.id = id;
        this.nominativo = nominativo;
        this.permanente = permanente;
        this.vacations = vacations;
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

    public ArrayList<Role>[] getRuoli() {
        return Ruoli;
    }

    public void setRuoli(ArrayList<Role>[] ruoli) {
        Ruoli = ruoli;
    }

   public List<Vacation> getVacations() {
        return vacations;
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
    }

    
}
