package com.openclassrooms.tourguide.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;

public class AttractionRepository {

    private final List<Attraction> internalAttractionMap;

    public AttractionRepository(@Autowired GpsUtil gpsUtil) {
        this.internalAttractionMap = gpsUtil.getAttractions();
    }

    public List<Attraction> getAllAttractions() {
        return internalAttractionMap;
    }
}
