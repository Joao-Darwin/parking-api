package com.estacionamento.app.resources;

import com.estacionamento.app.entities.Vehicle;
import com.estacionamento.app.entities.dtos.responses.ErrorResponse;
import com.estacionamento.app.exceptions.NotFoundException;
import com.estacionamento.app.exceptions.NotSaveException;
import com.estacionamento.app.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/vehicles")
public class VehicleResource {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<?> saveVehicle(@RequestBody Vehicle vehicle) {
        try {
            vehicle = vehicleService.saveVehicle(vehicle);
            return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
        } catch (NotSaveException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> findAll() {
        try {
            List<Vehicle> vehicles = vehicleService.findAll();
            return ResponseEntity.status(HttpStatus.FOUND).body(vehicles);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleUpdated) {
        try {
            vehicleUpdated = vehicleService.updateVehicle(id, vehicleUpdated);
            return ResponseEntity.status(HttpStatus.OK).body(vehicleUpdated);
        } catch (NotFoundException | NotSaveException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(errorResponse);
        }
    }
}
