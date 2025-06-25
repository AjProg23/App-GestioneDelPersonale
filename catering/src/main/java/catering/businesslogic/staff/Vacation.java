package catering.businesslogic.staff;

//aggiunto 

import java.sql.Date;

public class Vacation {
   
    public int id;
    public Date starDate;
    public Date endDate;
    public Boolean approved;


    

    public Vacation(Boolean approved, Date endDate, Date starDate) {
        this.approved = approved;
        this.endDate = endDate;
        this.starDate = starDate;
    }

    public Vacation(Boolean approved, Date endDate, int id, Date starDate) {
        this.approved = approved;
        this.endDate = endDate;
        this.id = id;
        this.starDate = starDate;
    }

    public Date getStarDate() {
        return starDate;
    }

    public void setStarDate(Date starDate) {
        this.starDate = starDate;
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


}
