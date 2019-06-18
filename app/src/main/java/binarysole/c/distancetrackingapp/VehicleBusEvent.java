package binarysole.c.distancetrackingapp;

public class VehicleBusEvent {

    Boolean isVehicle;
    String type;
    int confidence;

    public Boolean getVehicle() {
        return isVehicle;
    }

    public void setVehicle(Boolean vehicle) {
        isVehicle = vehicle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
