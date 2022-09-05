package com.jms.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jms.dto.CarCoordinates;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class CarCoordinatesSerializer implements Serializer<CarCoordinates> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, CarCoordinates coordinates) {
        try {
            if (coordinates == null){
                log.warn("Null received at serializing");
                return null;
            }
            return objectMapper.writeValueAsBytes(coordinates);
        } catch (Exception e) {
            throw new RuntimeException("Error when serializing Coordinates to byte[]");
        }
    }
}
