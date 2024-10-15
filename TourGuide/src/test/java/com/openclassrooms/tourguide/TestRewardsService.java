package com.openclassrooms.tourguide;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.helper.InMemoryUserRepository;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

public class TestRewardsService {

	@Test
	public void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(0);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		locationService.trackUserLocation(user.getUserName());
		List<UserReward> userRewards = user.getUserRewards();
		locationService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(0);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository);
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(locationService.isAttractionUnderProximityRange(attraction, attraction));
	}

	@Disabled // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(1);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository,
				Integer.MAX_VALUE);

		rewardsService.calculateRewards(userRepository.getAllUsers().get(0));
		List<UserReward> userRewards = rewardsService.getUserRewards(userRepository.getAllUsers().get(0).getUserName());
		locationService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}

}
