package catering.businesslogic.event;

//aggiunto 
public class SummaryScheme {
    private Event e; //evento a cui fa riferimento
    private int nrOfStaffMembersRequired;
    private String transportationNeeds;
    private String typeOfService;
    private String clientRequest;
    public Event getE() {
        return e;
    }
    public void setE(Event e) {
        this.e = e;
    }
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
    public SummaryScheme(Event e, int nrOfStaffMembersRequired, String transportationNeeds, String typeOfService,
            String clientRequest) {
        this.e = e;
        this.nrOfStaffMembersRequired = nrOfStaffMembersRequired;
        this.transportationNeeds = transportationNeeds;
        this.typeOfService = typeOfService;
        this.clientRequest = clientRequest;
    }

    
}
