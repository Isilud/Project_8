package com.openclassrooms.tourguide.unit;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.tourguide.helper.InMemoryUserRepository;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

@ExtendWith(MockitoExtension.class)
public class TestLocationService {

    @Mock
    private GpsUtil gpsUtil;

    @Mock
    private RewardsService rewardsService;

    @Mock
    private InMemoryUserRepository userRepository;

    private LocationService locationService;

    private final int proximityBuffer = 10;

    @BeforeEach
    public void setup() {
        locationService = new LocationService(gpsUtil, rewardsService, userRepository, proximityBuffer);
    }

    @Test
    public void testTrackUserLocation() {
        // Given
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "1234", "default@email");
        VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(10.0, 20.0), new Date());
        
        when(gpsUtil.getUserLocation(userId)).thenReturn(visitedLocation);
        when(userRepository.getUser(username)).thenReturn(user);
        
        // When
        VisitedLocation result = locationService.trackUserLocation(username);
        
        // Then
        verify(rewardsService).calculateRewards(user);
        verify(userRepository).getUser(username);
        assertEquals(visitedLocation, result);
    }

    @Test
    public void testGetUserLocation_NoPreviousLocations() {
        // Given
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "1234", "default@email");
        VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(10.0, 20.0), new Date());

        when(gpsUtil.getUserLocation(userId)).thenReturn(visitedLocation);
        when(userRepository.getUser(username)).thenReturn(user);

        // When
        VisitedLocation result = locationService.getUserLocation(username);

        // Then
        assertEquals(visitedLocation, result);
        verify(gpsUtil).getUserLocation(userId);
    }

    @Test
    public void testGetUserLocation_WithPreviousLocation() {
        // Given
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "1234", "default@email");
        VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(10.0, 20.0), new Date());
        user.addToVisitedLocations(visitedLocation);

        when(userRepository.getUser(username)).thenReturn(user);

        // When
        VisitedLocation result = locationService.getUserLocation(username);

        // Then
        assertEquals(visitedLocation, result);
        verify(gpsUtil, never()).getUserLocation(any(UUID.class));
    }

    @Test
    public void testGetNearbyAttractions() {
        // Given
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), new Location(10.0, 20.0), new Date());
        Attraction nearbyAttraction = new Attraction("Attraction1", "City", "State", 10.1, 20.1);
        Attraction farAttraction = new Attraction("Attraction2", "City", "State", 50.0, 50.0);
        
        when(gpsUtil.getAttractions()).thenReturn(Arrays.asList(nearbyAttraction, farAttraction));
        
        // When
        List<Attraction> result = locationService.getNearbyAttractions(visitedLocation);
        
        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(nearbyAttraction));
        assertFalse(result.contains(farAttraction));
    }

    @Test
    public void testIsAttractionUnderProximityRange() {
        // Given
        Location userLocation = new Location(10.0, 20.0);
        Attraction attractionInRange = new Attraction("Attraction1", "City", "State", 10.1, 20.1);
        Attraction attractionOutOfRange = new Attraction("Attraction2", "City", "State", 50.0, 50.0);

        // When & Then
        assertTrue(locationService.isAttractionUnderProximityRange(attractionInRange, userLocation));
        assertFalse(locationService.isAttractionUnderProximityRange(attractionOutOfRange, userLocation));
    }

    @Test
    public void testStaticGetDistance() {
        // Given
        Location loc1 = new Location(10.0, 20.0);
        Location loc2 = new Location(10.1, 20.1);

        // When
        double distance = LocationService.getDistance(loc1, loc2);

        // Then
        assertTrue(distance > 0);
    }

    @Test
    public void testIsAttractionUnderBufferRange() {
        // Given
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), new Location(10.0, 20.0), new Date());
        Attraction attractionInRange = new Attraction("Attraction1", "City", "State", 10.1, 20.1);
        Attraction attractionOutOfRange = new Attraction("Attraction2", "City", "State", 50.0, 50.0);

        // When & Then
        assertTrue(LocationService.isAttractionUnderBufferRange(visitedLocation, attractionInRange));
        assertFalse(LocationService.isAttractionUnderBufferRange(visitedLocation, attractionOutOfRange));
    }
}