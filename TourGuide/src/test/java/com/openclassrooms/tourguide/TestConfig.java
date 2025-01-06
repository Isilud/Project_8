package com.openclassrooms.tourguide;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.repository.AttractionRepository;
import com.openclassrooms.tourguide.repository.InMemoryUserRepository;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

@TestConfiguration
public class TestConfig {

    @Bean
    public UserRepository userRepository() {
        InternalTestHelper.setInternalUserNumber(0);
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        InternalTestHelper.initializeInternalUsers(userRepository);
        return userRepository;
    }

    @Bean
    public AttractionRepository attractionRepository(@Autowired GpsUtil gpsUtil) {
        return new AttractionRepository(gpsUtil);
    }

    @Bean
    public LocationService locationService(@Autowired GpsUtil gpsUtil, @Autowired RewardsService rewardsService,
            @Autowired UserRepository userRepository) {
        return new LocationService(gpsUtil, rewardsService, userRepository);
    }
}