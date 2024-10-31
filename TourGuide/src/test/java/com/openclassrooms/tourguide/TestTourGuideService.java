package com.openclassrooms.tourguide;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.openclassrooms.tourguide.helper.InMemoryUserRepository;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TripPriceService;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tripPricer.Provider;
import tripPricer.TripPricer;

public class TestTourGuideService {

	@Test
	public void getUserLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(0);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = locationService.trackUserLocation(user.getUserName());
		locationService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void addUser() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(0);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userRepository.add(user);
		userRepository.add(user2);

		User retrivedUser = userRepository.getUser(user.getUserName());
		User retrivedUser2 = userRepository.getUser(user2.getUserName());

		locationService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(0);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userRepository.add(user);
		userRepository.add(user2);

		List<User> allUsers = userRepository.getAllUsers();

		locationService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(0);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = locationService.trackUserLocation(user.getUserName());

		locationService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Disabled // Not yet implemented
	@Test
	public void getNearbyAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(0);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = locationService.trackUserLocation(user.getUserName());

		List<Attraction> attractions = locationService.getNearbyAttractions(visitedLocation);

		locationService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}

	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(0);
		UserRepository userRepository = new InMemoryUserRepository();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository);
		TripPriceService tripPriceService = new TripPriceService(userRepository, new TripPricer());

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tripPriceService.getTripDeals(user.getUserName());

		locationService.tracker.stopTracking();

		assertEquals(10, providers.size());
	}

}
