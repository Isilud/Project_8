package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.helper.InMemoryUserRepository;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;

import gpsUtil.GpsUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public UserRepository userRepository() {
        InternalTestHelper.setInternalUserNumber(0);
        return new InMemoryUserRepository();
    }

    @Bean
    public LocationService locationService(@Autowired GpsUtil gpsUtil, @Autowired RewardsService rewardsService,
            @Autowired UserRepository userRepository) {
        return new LocationService(gpsUtil, rewardsService, userRepository);
    }
}