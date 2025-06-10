package catering.businesslogic.event;

//aggiunto 

public class StaffMember {
    private int id;
    private String nominativo;
    private String[] features;
    private Boolean[] featuresValue;
   
    public StaffMember(int id, String nominativo, String[] features, Boolean[] featuresValue) {
        this.id = id;
        this.nominativo = nominativo;
        this.features = features;
        this.featuresValue = featuresValue;
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

    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(String[] features) {
        this.features = features;
    }

    public Boolean[] getFeaturesValue() {
        return featuresValue;
    }

    public void setFeaturesValue(Boolean[] featuresValue) {
        this.featuresValue = featuresValue;
    }

    
}
