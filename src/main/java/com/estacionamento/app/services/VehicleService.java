package com.estacionamento.app.services;

import com.estacionamento.app.entities.Company;
import com.estacionamento.app.entities.Vehicle;
import com.estacionamento.app.entities.dtos.responses.AllDataVehicle;
import com.estacionamento.app.entities.dtos.responses.CompanyDTO;
import com.estacionamento.app.entities.dtos.responses.VehicleDTO;
import com.estacionamento.app.entities.enums.VehiclesType;
import com.estacionamento.app.exceptions.NotFoundException;
import com.estacionamento.app.exceptions.NotSaveException;
import com.estacionamento.app.repositories.CompanyRepository;
import com.estacionamento.app.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Vehicle saveVehicle(Vehicle vehicle) throws NotSaveException {
        try {
            checkIfParkingIsAvailable(vehicle);

            vehicleRepository.save(vehicle);
            return vehicle;
        } catch (DataIntegrityViolationException exception) {
            throw new NotSaveException("Vehicle does not save, vehicle already exists");
        }
    }

    private void checkIfParkingIsAvailable(Vehicle vehicle) {
        VehiclesType typeVehicle = vehicle.getType();
        Long companyId = vehicle.getCompany().getId();
        Company companyVehicle = companyRepository.findById(companyId).get();

        if (typeVehicle == VehiclesType.CAR) {
            if (companyVehicle.verifySpacesCarIsFull()) {
                throw new NotSaveException("Car does not save, parking's company is full!");
            }
        } else {
            if (companyVehicle.verifySpacesMotorcyclesIsFull()) {
                throw new NotSaveException("Motorcycle does not save, parking's company is full!");
            }
        }
    }

    public List<VehicleDTO> findAll() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<VehicleDTO> allVehiclesDTO = new ArrayList<>();

        generatedAllVehiclesDTO(allVehicles, allVehiclesDTO);

        return allVehiclesDTO;
    }

    private void generatedAllVehiclesDTO(List<Vehicle> allVehicles, List<VehicleDTO> allVehiclesDTO) {
        for (Vehicle vehicle : allVehicles) {
            CompanyDTO companyDTO = new CompanyDTO(vehicle.getCompany().getId(), vehicle.getCompany().getName());
            VehicleDTO vehicleDTO = new VehicleDTO(vehicle.getId(), vehicle.getModel(), vehicle.getPlate(), vehicle.getType(), companyDTO);

            allVehiclesDTO.add(vehicleDTO);
        }
    }

    public AllDataVehicle findVehicleById(Long idVehicle) {
        try {
            Vehicle vehicle = vehicleRepository.findById(idVehicle).get();
            AllDataVehicle vehicleDTO = generateAllDataVehicle(vehicle);

            return vehicleDTO;
        } catch (NoSuchElementException exception) {
            throw new NotFoundException(String.format("Vehicle not finded. Id: %d", idVehicle));
        }
    }

    private AllDataVehicle generateAllDataVehicle(Vehicle vehicle) {
        Company company = vehicle.getCompany();
        CompanyDTO companyDTO = new CompanyDTO(company.getId(), company.getName());
        AllDataVehicle vehicleDTO = new AllDataVehicle(vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getPlate(),
                vehicle.getEntryDate(),
                vehicle.getType(),
                companyDTO);

        return vehicleDTO;
    }

    public Vehicle updateVehicle(Long idVehicle, Vehicle vehicleUpdated) {
        try {
            Vehicle vehicleToUpdate = vehicleRepository.findById(idVehicle).get();

            updateVehicleData(vehicleToUpdate, vehicleUpdated);

            vehicleRepository.save(vehicleToUpdate);
            return vehicleToUpdate;
        } catch (NoSuchElementException exception) {
            throw new NotFoundException(String.format("Vehicle to update not finded. Id: %d", idVehicle));
        } catch (DataIntegrityViolationException exception) {
            throw new NotSaveException("Vehicle does not save, vehicle already exists");
        }
    }

    private void updateVehicleData(Vehicle vehicleToUpdate, Vehicle vehicleUpdated) {
        vehicleToUpdate.setBrand(vehicleUpdated.getBrand());
        vehicleToUpdate.setModel(vehicleUpdated.getModel());
        vehicleToUpdate.setColor(vehicleUpdated.getColor());
        vehicleToUpdate.setPlate(vehicleUpdated.getPlate());
        vehicleToUpdate.setType(vehicleUpdated.getType());
        vehicleToUpdate.setCompany(vehicleUpdated.getCompany());
    }

    public void deleteVehicle(Long idVehicle) {
        try {
            vehicleRepository.deleteById(idVehicle);
        } catch (NoSuchElementException exception) {
            throw new NotFoundException(String.format("Vehicle to remove not finded. Id: %d", idVehicle));
        }
    }
}
