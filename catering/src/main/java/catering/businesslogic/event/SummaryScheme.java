package catering.businesslogic.event;

import java.util.logging.Logger;

import catering.persistence.PersistenceManager;
import catering.util.LogManager;

//aggiunto 
public class SummaryScheme {
    
    private static final Logger LOGGER = LogManager.getLogger(SummaryScheme.class);
    
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

    // Database operations
    public void saveNewSummaryScheme() {
        String query = "INSERT INTO SummaryScheme (nrOfStaffMembersRequired, transportationNeeds, typeOfService, clientRequest) VALUES (?, ?, ?, ?)";

        PersistenceManager.executeUpdate(query, nrOfStaffMembersRequired, transportationNeeds, typeOfService, clientRequest);

        LOGGER.info("Saved Summary scheme");
    }

    
}
