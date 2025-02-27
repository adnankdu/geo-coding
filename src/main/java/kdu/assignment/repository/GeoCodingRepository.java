package kdu.assignment.repository;

import kdu.assignment.model.GeoCodingResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class GeoCodingRepository {

    /**
     * Retrieve data from the geocoding cache unless the address is "goa".
     * @param address Key for the cache (address).
     * @return Cached GeoCodingResponse or null if not found.
     */
    @Cacheable(value = "geocoding", key = "#address", unless = "#address.equalsIgnoreCase('goa')")
    public GeoCodingResponse findGeocoding(String address) {
        return null; // Fetch logic can be implemented in the service layer.
    }

    /**
     * Save geocoding data to the cache.
     * @param address Key for the cache (address).
     * @param response GeoCodingResponse to be cached.
     */
    @CachePut(value = "geocoding", key = "#address")
    public void saveGeocoding(String address, GeoCodingResponse response) {
        // Cache saving handled by annotation.
    }

    /**
     * Retrieve data from the reverse geocoding cache.
     * @param key Key for the cache (latitude and longitude as a string).
     * @return Cached GeoCodingResponse or null if not found.
     */
    @Cacheable(value = "reverse-geocoding", key = "#key")
    public GeoCodingResponse findReverseGeocoding(String key) {
        return null; // Fetch logic can be implemented in the service layer.
    }

    /**
     * Save reverse geocoding data to the cache.
     * @param key Key for the cache (latitude and longitude as a string).
     * @param response GeoCodingResponse to be cached.
     */
    @CachePut(value = "reverse-geocoding", key = "#key")
    public void saveReverseGeocoding(String key, GeoCodingResponse response) {
        // Cache saving handled by annotation.
    }

    /**
     * Remove an entry from the geocoding cache.
     * @param address Key for the cache (address).
     */
    @CacheEvict(value = "geocoding", key = "#address")
    public void evictGeocoding(String address) {
        // Cache eviction handled by annotation.
    }

    /**
     * Remove an entry from the reverse geocoding cache.
     * @param key Key for the cache (latitude and longitude as a string).
     */
    @CacheEvict(value = "reverse-geocoding", key = "#key")
    public void evictReverseGeocoding(String key) {
        // Cache eviction handled by annotation.
    }

    /**
     * Clear all entries from both caches.
     */
    @CacheEvict(value = {"geocoding", "reverse-geocoding"}, allEntries = true)
    public void clearAllCaches() {
        // Cache clearing handled by annotation.
    }
}