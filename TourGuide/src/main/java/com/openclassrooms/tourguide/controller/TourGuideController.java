package com.openclassrooms.tourguide.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TripPriceService;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    private final TripPriceService tripPriceService;
    private final RewardsService rewardsService;
    private final LocationService locationService;

    public TourGuideController(TripPriceService tripPriceService, RewardsService rewardsService,
            LocationService locationService) {
        this.tripPriceService = tripPriceService;
        this.rewardsService = rewardsService;
        this.locationService = locationService;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) {
        return locationService.getUserLocation(userName);
    }

    // TODO: Change this method to no longer return a List of Attractions.
    // Instead: Get the closest five tourist attractions to the user - no matter how
    // far away they are.
    // Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the
    // attractions.
    // The reward points for visiting each Attraction.
    // Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public List<Attraction> getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = locationService.getUserLocation(userName);
        return locationService.getNearbyAttractions(visitedLocation);
    }

    @RequestMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return rewardsService.getUserRewards(userName);
    }

    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        return tripPriceService.getTripDeals(userName);
    }
}