package com.openclassrooms.tourguide.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.repository.UserRepository;

import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TripPriceService {
	private static final String TRIP_PRICER_API_KEY = "defaultAPIKEY";
	private final TripPricer tripPricer;
	private final UserRepository userRepository;

	public TripPriceService(UserRepository userRepository, TripPricer tripPricer) {
		this.userRepository = userRepository;
		this.tripPricer = tripPricer;
	}

	public List<Provider> getTripDeals(String username) {
		User user = userRepository.getUser(username);
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(TRIP_PRICER_API_KEY, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

}
