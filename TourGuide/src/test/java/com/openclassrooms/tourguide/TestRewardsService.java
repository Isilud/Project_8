package com.openclassrooms.tourguide;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.openclassrooms.tourguide.helper.InMemoryUserRepository;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;


@ActiveProfiles("test")
public class TestRewardsService {

	// Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestHelper.setInternalUserNumber(1);
		InMemoryUserRepository userRepository = new InMemoryUserRepository();
		userRepository.initializeInternalUsers();
		RewardsService rewardsService = new RewardsService(userRepository, gpsUtil, new RewardCentral());
		LocationService locationService = new LocationService(gpsUtil, rewardsService, userRepository,
				Integer.MAX_VALUE);

		rewardsService.calculateRewards(userRepository.getAllUsers().get(0));
		List<UserReward> userRewards = rewardsService.getUserRewards(userRepository.getAllUsers().get(0).getUserName());
		locationService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}

}
