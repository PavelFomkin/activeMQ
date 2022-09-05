package com.jms.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jms.dto.CarCoordinates;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class CarCoordinatesDeserializer implements Deserializer<CarCoordinates> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CarCoordinates deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                log.warn("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), CarCoordinates.class);
        } catch (Exception e) {
            throw new RuntimeException("Error when deserializing byte[] to Coordinates");
        }
    }
}
