package catering.businesslogic.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import catering.businesslogic.UseCaseLogicException;
import catering.persistence.PersistenceManager;
import catering.util.LogManager;

//aggiunto 
public class SummaryScheme {
    
    private static final Logger LOGGER = LogManager.getLogger(SummaryScheme.class);
    
    private int id;
    private int nrOfStaffMembersRequired;
    private String transportationNeeds;
    private String typeOfService;
    private String clientRequest;   
    
    public int getNrOfStaffMembersRequired() {
        return nrOfStaffMembersRequired;
    }
    public void setNrOfStaffMembersRequired(int nrOfStaffMembersRequired) {
        this.nrOfStaffMembersRequired = nrOfStaffMembersRequired;
    }
    public String getTransportationNeeds() {
        return transportationNeeds;
    }
    public void setTransportationNeeds(String transportationNeeds) {
        this.transportationNeeds = transportationNeeds;
    }
    public String getTypeOfService() {
        return typeOfService;
    }
    public void setTypeOfService(String typeOfService) {
        this.typeOfService = typeOfService;
    }
    public String getClientRequest() {
        return clientRequest;
    }
    public void setClientRequest(String clientRequest) {
        this.clientRequest = clientRequest;
    }
    public SummaryScheme( int nrOfStaffMembersRequired, String transportationNeeds, String typeOfService,
            String clientRequest) {
        this.nrOfStaffMembersRequired = nrOfStaffMembersRequired;
        this.transportationNeeds = transportationNeeds;
        this.typeOfService = typeOfService;
        this.clientRequest = clientRequest;
    }

    //costruttore che gestisce caso transportationNeeds fosse null
    public SummaryScheme( int nrOfStaffMembersRequired, String typeOfService,
            String clientRequest) {
        this.nrOfStaffMembersRequired = nrOfStaffMembersRequired;
        this.typeOfService = typeOfService;
        this.clientRequest = clientRequest;
    }

    // Database operations
    public void saveNewSummaryScheme() {
        String query = "INSERT INTO SummaryScheme (nr_of_staff_members_required, transportation_needs, type_of_service, client_request) VALUES (?, ?, ?, ?)";

        PersistenceManager.executeUpdate(query, nrOfStaffMembersRequired, transportationNeeds, typeOfService, clientRequest);
        this.id = PersistenceManager.getLastId();
        LOGGER.info("Saved Summary scheme");
    }

    public int getId() {
        return id;
    }

    public static SummaryScheme loadByEventId(int eventId) throws UseCaseLogicException {
    SummaryScheme ss = null;

    String sql = "SELECT id, nr_of_staff_members_required, transportation_needs, type_of_service, client_request FROM SummaryScheme WHERE event_id = ? LIMIT 1";

    try (Connection conn = PersistenceManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setInt(1, eventId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int id = rs.getInt("id");
                int nrStaff = rs.getInt("nr_of_staff_members_required");
                String transport = rs.getString("transportation_needs");
                String typeOfService = rs.getString("type_of_service");
                String clientRequest = rs.getString("client_request");

                ss = new SummaryScheme(nrStaff, transport, typeOfService, clientRequest);
                ss.setId(id); // if you have an id setter
            }
        }

    } catch (SQLException e) {
        throw new UseCaseLogicException("Error loading summary scheme for event id " + eventId, e);
    }

    return ss;
    }


    private void setId(int id2) {
        this.id = id2;
    }


    
}
