package com.jms.entity;

import com.jms.dto.Coordinates;
import com.jms.util.DistanceCalculator;
import lombok.Getter;

@Getter
public class CarTracker {
    private Long distance = 0L;
    private Coordinates lastCoordinates;

    public void track(Coordinates coordinates) {
        if (lastCoordinates != null) {
            distance += DistanceCalculator.getDistance(lastCoordinates, coordinates);
        }
        lastCoordinates = coordinates;
    }
}
