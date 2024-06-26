package com.estacionamento.app.resources.companies.impl;

import com.estacionamento.app.entities.Company;
import com.estacionamento.app.entities.dtos.responses.DataCompanyDTO;
import com.estacionamento.app.entities.dtos.responses.ErrorResponse;
import com.estacionamento.app.entities.dtos.responses.OnlyVehicleDTO;
import com.estacionamento.app.exceptions.NotFoundException;
import com.estacionamento.app.exceptions.NotSaveException;
import com.estacionamento.app.resources.companies.ICompanyResource;
import com.estacionamento.app.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "${data.baseurl}/companies")
public class CompanyResource implements ICompanyResource {

    private final CompanyService companyService;

    @Autowired
    CompanyResource(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<?> saveCompany(@RequestBody Company company) {
        try {
            company = companyService.saveCompany(company);
            return ResponseEntity.status(HttpStatus.CREATED).body(company);
        } catch (NotSaveException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<Page<DataCompanyDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "asc") String sort
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "name"));

        try {
            return ResponseEntity.status(HttpStatus.FOUND).body(companyService.findAll(pageable));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/{id}/historic")
    public ResponseEntity<?> findVehiclesByCompany(@PathVariable Long id) {
        try {
            List<OnlyVehicleDTO> vehicles = companyService.findAllVehiclesByCompany(id);
            return ResponseEntity.status(HttpStatus.OK).body(vehicles);
        } catch (NotFoundException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping(value = "/{id}/parking")
    public ResponseEntity<?> findVehiclesOnCompanyParking(@PathVariable Long id) {
        try {
            List<OnlyVehicleDTO> vehicles = companyService.findAllVehiclesOnCompanyParking(id);
            return ResponseEntity.status(HttpStatus.OK).body(vehicles);
        } catch (NotFoundException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable(value = "id") Long idCompany, @RequestBody Company companyUpdated) {
        try {
            companyUpdated = companyService.updateCompany(idCompany, companyUpdated);
            return ResponseEntity.status(HttpStatus.OK).body(companyUpdated);
        } catch (NotFoundException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(errorResponse);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        try {
            companyService.deleteCompany(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NotFoundException exception) {
            ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(errorResponse);
        }
    }
}
