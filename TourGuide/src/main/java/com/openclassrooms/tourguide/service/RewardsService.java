package com.openclassrooms.tourguide.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.controller.TourGuideController;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.repository.UserRepository;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

@Service
public class RewardsService {

	private final Logger logger = LoggerFactory.getLogger(TourGuideController.class);

	private final UserRepository userRepository;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	public RewardsService(UserRepository userRepository, GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.userRepository = userRepository;
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}

	public void calculateRewards(User user) {
		logger.debug("Calculating rewards for " + user.getUserId());
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();

		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (user.getUserRewards().stream()
						.filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if (LocationService.isAttractionUnderBufferRange(visitedLocation, attraction)) {
						user.addUserReward(
								new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}
		logger.debug("Rewards calculation for " + user.getUserId() + " is over");
	}

	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	public List<UserReward> getUserRewards(String username) {
		return userRepository.getUser(username).getUserRewards();
	}
}
