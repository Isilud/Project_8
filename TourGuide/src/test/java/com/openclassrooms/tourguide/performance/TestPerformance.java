package com.openclassrooms.tourguide.performance;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import com.openclassrooms.tourguide.TestConfigPerformance;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardsService;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = { TestConfigPerformance.class })
public class TestPerformance {

	@Autowired
	private LocationService locationService;

	@Autowired
	private RewardsService rewardsService;

	/*
	 * A note on performance improvements:
	 * 
	 * The number of users generated for the high volume tests can be easily
	 * adjusted via this method:
	 * 
	 * InternalTestHelper.setInternalUserNumber(100000);
	 * 
	 * 
	 * These tests can be modified to suit new solutions, just as long as the
	 * performance metrics at the end of the tests remains consistent.
	 * 
	 * These are performance metrics that we are trying to hit:
	 * 
	 * highVolumeTrackLocation: 100,000 users within 15 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 *
	 * highVolumeGetRewards: 100,000 users within 20 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@Test
	public void highVolumeTrackLocation() throws Exception {
		// Users should be incremented up to 100,000, and test finishes within 15
		// minutes

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		locationService.trackAllUserLocation();

		stopWatch.stop();
		locationService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeCalculateReward() throws Exception {
		// Users should be incremented up to 100,000, and test finishes within 20
		// minutes

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		rewardsService.calculateAllUsersRewards();

		stopWatch.stop();

		System.out.println("highVolumeCalculateReward: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}
