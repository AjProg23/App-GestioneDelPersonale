package catering.businesslogic.event;

import java.util.logging.Logger;

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

    
}
