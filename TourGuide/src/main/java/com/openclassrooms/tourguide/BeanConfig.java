package com.openclassrooms.tourguide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

@Configuration
public class BeanConfig {

    @Bean
    public GpsUtil gpsUtils() {
        return new GpsUtil();
    }

    @Bean
    public TripPricer tripPricer() {
        return new TripPricer();
    }

    @Bean
    public RewardCentral rewardCentral() {
        return new RewardCentral();
    }
}