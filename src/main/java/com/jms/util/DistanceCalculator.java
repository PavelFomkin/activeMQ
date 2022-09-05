package com.jms.util;

import com.jms.dto.Coordinates;

public class DistanceCalculator {
    public static long getDistance(Coordinates start, Coordinates finish) {
        long cathetus_1 = Math.abs(start.getX() - finish.getX());
        long cathetus_2 = Math.abs(start.getY() - finish.getY());

        double sqrt = Math.sqrt(Math.pow(cathetus_1, 2) + Math.pow(cathetus_2, 2));
        return Math.round(sqrt);
    }
}
