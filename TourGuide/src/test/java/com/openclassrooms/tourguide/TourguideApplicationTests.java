package com.openclassrooms.tourguide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = { TestConfig.class })
class TourguideApplicationTests {

    @MockBean
    private GpsUtil gpsUtil;

    @MockBean
    private RewardCentral rewardCentral;

    @MockBean
    private TripPricer tripPricer;

	@Test
	void contextLoads() {
	}

}
