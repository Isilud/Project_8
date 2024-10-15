package com.openclassrooms.tourguide.task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.repository.UserRepository;
import com.openclassrooms.tourguide.service.LocationService;

public class Tracker extends Thread {
	private final Logger logger = LoggerFactory.getLogger(Tracker.class);
	private static final long TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES.toSeconds(5);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final LocationService locationService;
	private final UserRepository userRepository;
	private boolean stop = false;

	public Tracker(UserRepository userRepository, LocationService locationService) {
		this.userRepository = userRepository;
		this.locationService = locationService;

		executorService.submit(this);
	}

	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}

	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		while (true) {
			if (Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}

			List<User> users = userRepository.getAllUsers();
			logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
			stopWatch.start();
			users.forEach(u -> locationService.trackUserLocation(u.getUserName()));
			stopWatch.stop();
			logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
			stopWatch.reset();
			try {
				logger.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
			} catch (InterruptedException e) {
				break;
			}
		}

	}
}
