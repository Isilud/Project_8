package com.openclassrooms.tourguide.integration.controller;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import com.openclassrooms.tourguide.model.UserReward;
import com.openclassrooms.tourguide.repository.UserRepository;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tripPricer.Provider;
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

        UUID userUUID;

        User user;

        @BeforeAll
        public void setup() {
                // Set up the user
                userUUID = UUID.randomUUID();
                user = new User(userUUID, "userName", "userNumber", "userEmail");
                userRepository.add(user);
        }

        @Test
        public void indexTest() throws Exception {
                mockMvc.perform(get("/"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Greetings from TourGuide!"));
        }

        @Test
        public void getLocationTest() throws Exception {
                // Mock the response of gpsUtil
                VisitedLocation mockLocation = new VisitedLocation(userUUID, null, null);
                Mockito.when(gpsUtil.getUserLocation(userUUID)).thenReturn(mockLocation);

                mockMvc.perform(get("/getLocation").param("userName", "userName"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(mockLocation.userId.toString()));
        }

        @Test
        public void getNearbyAttractionsTest() throws Exception {
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

        @Test
        public void getRewardsTest() throws Exception {
                VisitedLocation location1 = new VisitedLocation(userUUID, null, null);
                VisitedLocation location2 = new VisitedLocation(userUUID, null, null);
                Attraction attraction1 = new Attraction("Attraction1", "City1", "State1", 0,
                                0);
                Attraction attraction2 = new Attraction("Attraction2", "City2", "State2", 0,
                                0);
                UserReward reward1 = new UserReward(location1, attraction1, 0);
                UserReward reward2 = new UserReward(location2, attraction2, 0);
                user.addUserReward(reward1);
                user.addUserReward(reward2);
                userRepository.add(user);

                mockMvc.perform(get("/getRewards").param("userName", "userName"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].attraction.attractionName").value("Attraction1"))
                                .andExpect(jsonPath("$[1].attraction.attractionName").value("Attraction2"));
        }

        @Test
        public void getTripDealsTest() throws Exception {
                VisitedLocation location1 = new VisitedLocation(userUUID, null, null);
                VisitedLocation location2 = new VisitedLocation(userUUID, null, null);
                Attraction attraction1 = new Attraction("Attraction1", "City1", "State1", 0,
                                0);
                Attraction attraction2 = new Attraction("Attraction2", "City2", "State2", 0,
                                0);
                UserReward reward1 = new UserReward(location1, attraction1, 0);
                UserReward reward2 = new UserReward(location2, attraction2, 0);
                user.addUserReward(reward1);
                user.addUserReward(reward2);
                userRepository.add(user);
                Provider provider1 = new Provider(UUID.randomUUID(), "Provider1", 10.);
                Provider provider2 = new Provider(UUID.randomUUID(), "Provider2", 20.);
                List<Provider> providers = List.of(provider1, provider2);
                Mockito.when(tripPricer.getPrice(any(String.class), any(UUID.class), anyInt(), anyInt(), anyInt(),
                                anyInt())).thenReturn(providers);

                mockMvc.perform(get("/getTripDeals").param("userName", "userName"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].name").value("Provider1"))
                                .andExpect(jsonPath("$[1].name").value("Provider2"));
        }
}
