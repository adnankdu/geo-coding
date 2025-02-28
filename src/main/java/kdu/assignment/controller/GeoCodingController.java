package kdu.assignment.controller;

import kdu.assignment.exception.ExternalApiException;
import kdu.assignment.exception.InvalidRequestException;
import kdu.assignment.model.GeoCodingResponse;
import kdu.assignment.service.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeoCodingController {
    public final GeocodingService geocodingService;
    @Autowired
    public GeoCodingController(GeocodingService geocodingService){
     this.geocodingService = geocodingService;
    }

    @GetMapping("/geocode")
    public ResponseEntity<GeoCodingResponse> getGeocoding(@RequestParam("address") String address) throws InvalidRequestException, ExternalApiException {
        if (address == null || address.isBlank()) {
            throw new InvalidRequestException("Address parameter cannot be null or empty.");
        }

        try {
            GeoCodingResponse response = geocodingService.getGeoCoding(address);
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            throw new ExternalApiException("Failed to fetch geocoding data from the external API." , e);
        }
    }

    @GetMapping("/reverse-geocoding")
    public ResponseEntity<GeoCodingResponse> getReverseGeocoding(@RequestParam("latitude") Double latitude,@RequestParam("longitude") Double longitude) throws InvalidRequestException, ExternalApiException {
        if (latitude == null || longitude == null) {
            throw new InvalidRequestException("Latitude and longitude parameters cannot be null.");
        }
        if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude > 180.0) {
            throw new InvalidRequestException("Latitude and longitude parameters are not valid");
        }

        try {
            GeoCodingResponse response = geocodingService.getReverseGeoCoding(latitude, longitude);
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            throw new ExternalApiException("Failed to fetch reverse geocoding data from the external API.");
        }
    }


}
