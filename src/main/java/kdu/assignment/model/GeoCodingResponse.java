package kdu.assignment.model;

public class GeoCodingResponse {
    private Double latitude; // Latitude for reverse geocoding
    private Double longitude;// Longitude for reverse geocoding
    private String address;
    private String region;

    public GeoCodingResponse(double longitude, double latitude, String address, String region) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.region = region;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
