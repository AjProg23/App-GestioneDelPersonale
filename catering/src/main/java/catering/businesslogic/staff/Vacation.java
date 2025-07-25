package catering.businesslogic.staff;

import java.sql.Connection;

//aggiunto 

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import catering.persistence.PersistenceManager;
import catering.util.LogManager;

public class Vacation {
    private static final Logger LOGGER = LogManager.getLogger(StaffManager.class);

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
    String sql = "SELECT id, start_date, end_date, approved FROM Vacation WHERE staff_member_id = ?";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    try (Connection conn = PersistenceManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, staffMemberId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String startDateStr = rs.getString("start_date");
                String endDateStr = rs.getString("end_date");
                Boolean approved = rs.getBoolean("approved");

                java.sql.Date startDate = parseDateFlexible(startDateStr, sdf);
                java.sql.Date endDate = parseDateFlexible(endDateStr, sdf);

                Vacation vac = new Vacation(approved, endDate, id, startDate);
                vacations.add(vac);
            }
        }

    } catch (SQLException | ParseException e) {
        throw new RuntimeException("Error loading vacations for staff member ID " + staffMemberId, e);
    }

    return vacations;
    }


    // Helper method to parse either "yyyy-MM-dd" or millisecond timestamp stored as string
    private static java.sql.Date parseDateFlexible(String dateStr, SimpleDateFormat sdf) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) return null;

        // If numeric timestamp, parse as long millis
        if (dateStr.matches("\\d+")) {
            long millis = Long.parseLong(dateStr);
            return new java.sql.Date(millis);
        } else {
            // Parse date string
            java.util.Date parsed = sdf.parse(dateStr);
            return new java.sql.Date(parsed.getTime());
        }
    }




    public void updateAcceptedVacationRequest() {
        if (id == 0) {
            LOGGER.warning("updateAcceptedVacationRequest: id non valorizzato (id = 0)");
            return;
        }

        String updateSql = "UPDATE Vacation "
                        + "SET approved = ?, start_date = ?, end_date = ? "
                        + "WHERE id = ?";

        PersistenceManager.executeUpdate(updateSql,
                                        approved,
                                        startDate,
                                        endDate,
                                        id);

        LOGGER.info("Vacation aggiornata: ID=" + id);
    }

}
