package com.openclassrooms.tourguide.unit;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserPreferences;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.service.TripPriceService;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;
import tripPricer.TripPricer;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = "test")
public class TestTripPriceService {

    @Mock
    private TripPricer tripPricer;

    @Mock
    private UserRepository userRepository;

    private TripPriceService tripPriceService;

    @BeforeEach
    public void setup() {
        tripPriceService = new TripPriceService(userRepository, tripPricer);
    }

    @Test
    public void testGetTripDeals_UserWithRewards() {
        // Given
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "1234", "default@email");

        UserPreferences preferences = new UserPreferences();
        preferences.setNumberOfAdults(2);
        preferences.setNumberOfChildren(1);
        preferences.setTripDuration(5);
        user.setUserPreferences(preferences);

        UserReward reward1 = new UserReward(new VisitedLocation(userId, new Location(0, 0), new Date()),
                new Attraction("Attraction1", "City", "State", 0, 0), 100);
        UserReward reward2 = new UserReward(new VisitedLocation(userId, new Location(0, 0), new Date()),
                new Attraction("Attraction2", "City", "State", 0, 0), 200);
        user.addUserReward(reward1);
        user.addUserReward(reward2);

        List<Provider> expectedProviders = Arrays.asList(
                new Provider(UUID.randomUUID(), "Provider1", 100.0),
                new Provider(UUID.randomUUID(), "Provider2", 200.0));

        // When
        when(userRepository.getUser(username)).thenReturn(user);
        when(tripPricer.getPrice(
                "defaultAPIKEY",
                userId,
                preferences.getNumberOfAdults(),
                preferences.getNumberOfChildren(),
                preferences.getTripDuration(),
                300 // Cumulative reward points (100 + 200)
        )).thenReturn(expectedProviders);

        // Then
        List<Provider> result = tripPriceService.getTripDeals(username);

        assertEquals(expectedProviders, result);
        assertEquals(expectedProviders, user.getTripDeals());
    }

    @Test
    public void testGetTripDeals_UserWithNoRewards() {
        // Given
        String username = "testUser";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, username, "1234", "default@email");

        UserPreferences preferences = new UserPreferences();
        preferences.setNumberOfAdults(1);
        preferences.setNumberOfChildren(2);
        preferences.setTripDuration(7);
        user.setUserPreferences(preferences);

        List<Provider> expectedProviders = Arrays.asList(
                new Provider(UUID.randomUUID(), "Provider1", 500.0));

        // When
        when(userRepository.getUser(username)).thenReturn(user);
        when(tripPricer.getPrice(
                "defaultAPIKEY",
                userId,
                preferences.getNumberOfAdults(),
                preferences.getNumberOfChildren(),
                preferences.getTripDuration(),
                0 // No rewards points
        )).thenReturn(expectedProviders);

        // Then
        List<Provider> result = tripPriceService.getTripDeals(username);

        assertEquals(expectedProviders, result);
        assertEquals(expectedProviders, user.getTripDeals());
    }
}
