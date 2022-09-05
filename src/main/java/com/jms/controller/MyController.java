package com.jms.controller;

import com.jms.service.CarTrackerManager;
import com.jms.dto.CarCoordinates;
import com.jms.dto.Coordinates;
import com.jms.producer.CarCoordinatesKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class MyController {
    @Autowired
    private CarCoordinatesKafkaProducer producer;
    @Autowired
    private CarTrackerManager manager;

    @PostMapping("/cars/{numberplate}/coordinates")
    public ResponseEntity<?> sendCoordinatesToKafka(@PathVariable("numberplate") String numberplate,
                                                    @RequestBody Coordinates coordinates) {
        producer.send(new CarCoordinates(numberplate, coordinates));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cars/{numberplate}/distance")
    public ResponseEntity<Long> getDistance(@PathVariable("numberplate") String numberplate) {
        return ResponseEntity.ok(manager.getCarDistances(numberplate));
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> handleError(RuntimeException ex) {
        log.error(ex.getMessage()); // log only message to not trash logs
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
