package com.openclassrooms.tourguide;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.openclassrooms.tourguide.helper.InMemoryUserRepository;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

@TestConfiguration
public class TestConfigPerformance {

    @Bean
    UserRepository userRepository() {
        InternalTestHelper.setInternalUserNumber(100000);
        return new InMemoryUserRepository();
    }

    @Bean
    LocationService locationService(@Autowired GpsUtil gpsUtil, @Autowired RewardsService rewardsService,
            @Autowired UserRepository userRepository) {
        return new LocationService(gpsUtil, rewardsService, userRepository);
    }

    @Bean
    GpsUtil gpsUtils() {
        return new GpsUtil();
    }

    @Bean
    TripPricer tripPricer() {
        return new TripPricer();
    }

    @Bean
    RewardCentral rewardCentral() {
        return new RewardCentral();
    }
}