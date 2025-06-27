package catering.businesslogic.staff;

import catering.businesslogic.event.Event;
import catering.businesslogic.event.SummaryScheme;

public interface StaffEventReceiver{
    void updateEventCreated(Event e);
    void updateSummarySchemeCreated(SummaryScheme summaryScheme);
    public void updateStaffMemberCreated(StaffMember sm);
    public void updateStaffMemberAdded(StaffMember sm);
    public void UpdateStaffMemberPermanentJob(StaffMember sm);
    public void updateTeamCreated(Team t);
    public void updateMemberRemoved(Team t, Integer smID);
}