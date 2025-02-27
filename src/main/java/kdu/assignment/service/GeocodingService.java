package kdu.assignment.service;


import com.fasterxml.jackson.databind.JsonNode;
import kdu.assignment.exception.ExternalApiException;
import kdu.assignment.exception.InvalidAccessKeyException;
import kdu.assignment.exception.InvalidRequestException;
import kdu.assignment.logs.Logs;
import kdu.assignment.model.GeoCodingResponse;
import kdu.assignment.repository.GeoCodingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {

    private final GeoCodingRepository geoCodingRepository;

    /**
     * Constructor to initialize the repository.
     *
     * @param geoCodingRepository Repository for caching geocoding data.
     */
    @Autowired
    public GeocodingService(GeoCodingRepository geoCodingRepository) {
        this.geoCodingRepository = geoCodingRepository;
    }

    @Value("${geocoding-url}")
    public String geocodingUrl;

    @Value("${reverse-geocoding-url}")
    public String reverseGeoCodingUrl;

    @Value("${api-access-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Retrieves the API key.
     *
     * @return API access key.
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Retrieves geocoding information for a given address.
     *
     * @param address Address to geocode.
     * @return GeoCodingResponse containing geocoding details.
     * @throws InvalidRequestException If the address is invalid.
     * @throws ExternalApiException If an error occurs while calling the external API.
     */
    @Cacheable(value = "geocoding", key = "#address", unless = "#result==null || #result.region==null || #result.region.equals('Goa')")
    public GeoCodingResponse getGeoCoding(String address) throws InvalidRequestException, ExternalApiException {
        if (address.length() < 3) {
            throw new InvalidRequestException("Query length must be greater than 2");
        }

        GeoCodingResponse cachedResponse = geoCodingRepository.findGeocoding(address);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        String query = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String apiAccessKey = getApiKey();
        if (apiAccessKey == null || apiAccessKey.isEmpty()) {
            throw new InvalidAccessKeyException("The access key is null or empty. Please provide a valid key.");
        }

        String url = String.format("%s%s&query=%s",geocodingUrl, apiAccessKey, query);
        JsonNode response;

        try {
            response = restTemplate.getForObject(url, JsonNode.class);
            if (response == null) {
                throw new ExternalApiException("Received null response from the external API for address: " + address);
            }
        } catch (Exception e) {
            throw new ExternalApiException("An error occurred while calling the external API for address: " + address, e);
        }

        JsonNode data = response.get("data").get(0);
        if (data == null || !data.has("longitude") || !data.has("latitude")) {
            throw new ExternalApiException("No valid data found for address: " + address);
        }

        GeoCodingResponse geoCodingResponse = new GeoCodingResponse(
                data.get("longitude").asDouble(),
                data.get("latitude").asDouble(),
                address,
                data.get("region").asText()
        );

        try {
            if (!"goa".equalsIgnoreCase(address)) {
                geoCodingRepository.saveGeocoding(address, geoCodingResponse);
            }
        } catch (Exception e) {
            Logs.logger.error("Failed to save data to cache for address: {}. Error: {}", address, e.getMessage());
        }

        return geoCodingResponse;
    }

    /**
     * Retrieves reverse geocoding information for given coordinates.
     *
     * @param latitude Latitude of the location.
     * @param longitude Longitude of the location.
     * @return GeoCodingResponse containing reverse geocoding details.
     * @throws ExternalApiException If an error occurs while calling the external API.
     */

    @Cacheable(value = "reverse-geocoding", key = "#latitude + ',' + #longitude")
    public GeoCodingResponse getReverseGeoCoding(Double latitude, Double longitude) throws ExternalApiException {
        String key = String.format("%s,%s", latitude, longitude);
        GeoCodingResponse cachedResponse = geoCodingRepository.findReverseGeocoding(key);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        String url = String.format("%s%s&query=%s,%s".formatted(reverseGeoCodingUrl,apiKey, latitude, longitude));
        JsonNode response;

        try {
            response = restTemplate.getForObject(url, JsonNode.class);
            if (response == null) {
                throw new ExternalApiException("API response is null for coordinates: " + latitude + ", " + longitude);
            }
        } catch (Exception e) {
            throw new ExternalApiException("An error occurred while calling the API for coordinates: " + latitude + ", " + longitude, e);
        }

        JsonNode data = response.get("data").get(0);
        if (data == null) {
            throw new ExternalApiException("No data found for coordinates: " + latitude + ", " + longitude);
        }

        GeoCodingResponse geoCodingResponse = new GeoCodingResponse(
                latitude,
                longitude,
                data.get("label").asText(),
                data.get("region").asText()
        );

        geoCodingRepository.saveReverseGeocoding(key, geoCodingResponse);
        return geoCodingResponse;
    }
}
