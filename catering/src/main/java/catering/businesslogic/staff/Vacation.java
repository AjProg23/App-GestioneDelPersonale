package catering.businesslogic.staff;

import java.sql.Connection;

//aggiunto 

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import catering.persistence.PersistenceManager;

public class Vacation {
   
    public int id;
    public Date startDate;
    public Date endDate;
    public Boolean approved;


    

    public Vacation(Boolean approved, Date endDate, Date startDate) {
        this.approved = approved;
        this.endDate = endDate;
        this.startDate = startDate;
    }

    public Vacation(Boolean approved, Date endDate, int id, Date startDate) {
        this.approved = approved;
        this.endDate = endDate;
        this.id = id;
        this.startDate = startDate;
    }

    public Date getstartDate() {
        return startDate;
    }

    public void setstartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static List<Vacation> loadByStaffMemberId(Integer staffMemberId) {
    List<Vacation> vacations = new ArrayList<>();

    String sql = "SELECT id, start_date, end_date FROM Vacation WHERE staff_member_id = ?";

    try (Connection conn = PersistenceManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, staffMemberId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String startDateStr = rs.getString("start_date");
                String endDateStr = rs.getString("end_date");

                Date startDate = Date.valueOf(startDateStr);
                Date endDate = Date.valueOf(endDateStr);

                Boolean approved = false;  // default se la colonna non esiste

                Vacation vac = new Vacation(approved, endDate, id, startDate);
                vacations.add(vac);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error loading vacations for staff member ID " + staffMemberId, e);
    }

    return vacations;
}


}
