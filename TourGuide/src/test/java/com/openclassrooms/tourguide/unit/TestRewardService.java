package com.openclassrooms.tourguide.unit;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.service.RewardsService;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = "test")
public class TestRewardService {

    @Mock
    private GpsUtil gpsUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RewardCentral rewardsCentral;

    private RewardsService rewardsService;

    @BeforeEach
    public void setup() {
        rewardsService = new RewardsService(userRepository, gpsUtil, rewardsCentral);
    }

    @Test
    public void testCalculateRewards_UserWithNoPreviousRewards() {
        // Given
        User user = new User(UUID.randomUUID(), "testUser", "1234", "default@email");
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(10.0, 20.0), new Date());
        user.addToVisitedLocations(visitedLocation);
        Attraction attraction = new Attraction("Attraction1", "City", "State", 10.1, 20.1);

        // When
        when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(attraction));
        when(rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId())).thenReturn(100);

        // Then
        rewardsService.calculateRewards(user);

        List<UserReward> rewards = user.getUserRewards();
        assertEquals(1, rewards.size());
        assertEquals(100, rewards.get(0).getRewardPoints());
    }

    @Test
    public void testCalculateRewards_UserWithExistingRewards() {
        // Given
        User user = new User(UUID.randomUUID(), "testUser", "1234", "default@email");
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(10.0, 20.0), new Date());
        Attraction attraction = new Attraction("Attraction1", "City", "State", 10.1, 20.1);

        UserReward existingReward = new UserReward(visitedLocation, attraction, 100);
        user.addUserReward(existingReward);
        user.addToVisitedLocations(visitedLocation);

        // When
        when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(attraction));

        // Then
        rewardsService.calculateRewards(user);

        assertEquals(1, user.getUserRewards().size()); // Should still only have 1 reward
        verify(rewardsCentral, never()).getAttractionRewardPoints(any(UUID.class), any(UUID.class));
    }

    @Test
    public void testCalculateRewards_NoNearbyAttractions() {
        // Given
        User user = new User(UUID.randomUUID(), "testUser", "1234", "default@email");
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(10.0, 20.0), new Date());
        user.addToVisitedLocations(visitedLocation);

        Attraction distantAttraction = new Attraction("DistantAttraction", "City", "State", 50.0, 50.0);

        // When
        when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(distantAttraction));

        // Then
        rewardsService.calculateRewards(user);
        assertTrue(user.getUserRewards().isEmpty());
    }

    @Test
    public void testGetUserRewards() {
        // Given
        String username = "testUser";
        User user = new User(UUID.randomUUID(), username, "1234", "default@email");
        UserReward reward = new UserReward(
                new VisitedLocation(user.getUserId(), new Location(10.0, 20.0), new Date()),
                new Attraction("Attraction1", "City", "State", 10.1, 20.1), 100);
        user.addUserReward(reward);

        // When
        when(userRepository.getUser(username)).thenReturn(user);

        // Then
        List<UserReward> rewards = rewardsService.getUserRewards(username);
        assertEquals(1, rewards.size());
        assertEquals(reward, rewards.get(0));
    }
}
