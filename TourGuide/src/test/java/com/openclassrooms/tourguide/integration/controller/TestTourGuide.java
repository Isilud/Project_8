package com.openclassrooms.tourguide.integration.controller;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.tourguide.TestConfig;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.repository.UserRepository;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tripPricer.TripPricer;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = { TestConfig.class })
public class TestTourGuide {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @MockBean
        private GpsUtil gpsUtil;

        @MockBean
        private RewardCentral rewardCentral;

        @MockBean
        private TripPricer tripPricer;

        @Test
        public void indexTest() throws Exception {
                mockMvc.perform(get("/"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Greetings from TourGuide!"));
        }

        @Test
        public void getLocationTest() throws Exception {
                // Set up the user
                UUID userUUID = UUID.randomUUID();
                userRepository.add(new User(userUUID, "userName", "userNumber", "userEmail"));
                // Mock the response of gpsUtil
                VisitedLocation mockLocation = new VisitedLocation(userUUID, null, null);
                Mockito.when(gpsUtil.getUserLocation(userUUID)).thenReturn(mockLocation);

                mockMvc.perform(get("/getLocation").param("userName", "userName"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(mockLocation.userId.toString()));
        }

        @Test
        public void getNearbyAttractionsTest() throws Exception {
                // Set up the user
                UUID userUUID = UUID.randomUUID();
                userRepository.add(new User(userUUID, "userName", "userNumber", "userEmail"));
                // Mock the response of gpsUtil
                Attraction attraction1 = new Attraction("Attraction1", "City1", "State1", 0,
                                0);
                Attraction attraction2 = new Attraction("Attraction2", "City2", "State2", 0,
                                0);
                List<Attraction> attractions = List.of(attraction1, attraction2);
                VisitedLocation mockLocation = new VisitedLocation(userUUID, new Location(0, 0), null);
                Mockito.when(gpsUtil.getUserLocation(userUUID)).thenReturn(mockLocation);
                Mockito.when(gpsUtil.getAttractions()).thenReturn(attractions);

                mockMvc.perform(get("/getNearbyAttractions").param("userName", "userName"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].attractionName").value("Attraction1"))
                                .andExpect(jsonPath("$[1].attractionName").value("Attraction2"));
        }
}
