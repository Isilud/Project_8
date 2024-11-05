package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.task.Tracker;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

@Service
public class LocationService {

	private final Logger logger = LoggerFactory.getLogger(LocationService.class);


	private final static double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private final int ATTRACTION_PROXIMITY_RANGE = 200;

	// proximity in miles
	private static int proximityBuffer = 10;
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final UserRepository userRepository;
	public final Tracker tracker;

	public LocationService(GpsUtil gpsUtil, RewardsService rewardsService, UserRepository userRepository) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.userRepository = userRepository;
		this.tracker = new Tracker(userRepository, this);
		Locale.setDefault(Locale.US);
		addShutDownHook();
	}

	public LocationService(GpsUtil gpsUtil, RewardsService rewardsService, UserRepository userRepository,
			int proximityBuffer) {
		this(gpsUtil, rewardsService, userRepository);
		LocationService.proximityBuffer = proximityBuffer;
	}

	public VisitedLocation trackUserLocation(String username) {
		User user = userRepository.getUser(username);
		logger.debug("Tracking " + user.getUserId() + " location.");
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		logger.debug("Returning tracked location for " + user.getUserId() + " : " + visitedLocation.toString());
		return visitedLocation;
	}

	public VisitedLocation getUserLocation(String username) {
		User user = userRepository.getUser(username);
		logger.debug("Searching latest location for " + user.getUserId());
		VisitedLocation visitedLocation = (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation()
				: trackUserLocation(username);
        logger.debug("Returning latest location for " + user.getUserId() + " : " + visitedLocation.toString());
		return visitedLocation;
	}

	public List<Attraction> getNearbyAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		logger.debug("Calculating nearby attractions for " + visitedLocation.toString());
		for (Attraction attraction : gpsUtil.getAttractions()) {
			if (isAttractionUnderProximityRange(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		logger.debug("Nearby attractions for " + visitedLocation.toString() + " : " + nearbyAttractions.toString());
		return nearbyAttractions;
	}

	public boolean isAttractionUnderProximityRange(Attraction attraction, Location location) {
		return getDistance(attraction, location) <= ATTRACTION_PROXIMITY_RANGE;
	}

	public static boolean isAttractionUnderBufferRange(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) <= proximityBuffer;
	}

	public static double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				tracker.stopTracking();
			}
		});
	}
}
