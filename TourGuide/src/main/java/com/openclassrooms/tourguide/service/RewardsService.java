package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.repository.AttractionRepository;
import com.openclassrooms.tourguide.repository.UserRepository;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

@Service
public class RewardsService {

	private final Logger logger = LoggerFactory.getLogger(RewardsService.class);

	private final UserRepository userRepository;
	private final AttractionRepository attractionRepository;
	private final RewardCentral rewardsCentral;
	private final ExecutorService executorService;

	public RewardsService(UserRepository userRepository, AttractionRepository attractionRepository,
			RewardCentral rewardCentral) {
		this.attractionRepository = attractionRepository;
		this.userRepository = userRepository;
		this.rewardsCentral = rewardCentral;
		this.executorService = Executors.newFixedThreadPool(1000);
	}

	public void calculateRewards(User user) {
		logger.debug("Calculating rewards for " + user.getUserId());
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = attractionRepository.getAllAttractions();

		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (LocationService.isAttractionUnderBufferRange(visitedLocation, attraction)) {
					user.addUserReward(
							new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
				}
			}
		}
		logger.debug("Rewards calculation for " + user.getUserId() + " is over");
	}

	public void calculateAllUsersRewards() throws InterruptedException, ExecutionException {
		List<User> allUsers = userRepository.getAllUsers();
		List<Future<?>> futures = new ArrayList<>();
		for (User user : allUsers) {
			Future<?> future = executorService.submit(() -> {
				try {
					calculateRewards(user);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			futures.add(future);
		}
		for (Future<?> future : futures) {
			future.get();
		}
	}

	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	public List<UserReward> getUserRewards(String username) {
		return userRepository.getUser(username).getUserRewards();
	}
}
