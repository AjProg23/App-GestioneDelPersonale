package catering.businesslogic.staff;

import java.util.ArrayList;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.user.User;

public class StaffMemberManager {
    private StaffMember currentStaffMember;
    private ArrayList<StaffMemberReceiver> staffMemberReceivers;

    public StaffMemberManager() {
    }

    public StaffMember createStaffMember() throws UseCaseLogicException{
        return this.createStaffMember(0, "Mario Rossi",null,null);
    } 

    public StaffMember createStaffMember(int id, String nominativo, String[] features, Boolean[] featuresValue) throws UseCaseLogicException{
        User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if(!user.isOrganizer()){
            throw new UseCaseLogicException("L'utente " + user.getUserName() + " non è un organizzatore. Creazione evento negata.");
        }
        StaffMember sm=new StaffMember(id, nominativo, features, featuresValue);
        this.setCurrentStaffMember(sm);
        this.notifyStaffMemberCreated(sm);
        return sm;
    }

    private void setCurrentStaffMember(StaffMember sm) {
        this.currentStaffMember=sm;
    }

    private void notifyStaffMemberCreated(StaffMember sm) {
        for (StaffMemberReceiver smr : this.staffMemberReceivers) {
            smr.updateStaffMemberCreated(sm);
        }
    }
}
