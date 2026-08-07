package com.grivo.controller;

import com.grivo.dto.PropertyCreateRequest;
import com.grivo.dto.PropertyResponse;
import com.grivo.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyCreateRequest request,
                                                     @RequestParam Long landlordId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.createProperty(request, landlordId));
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getMine(@RequestParam Long landlordId) {
        return ResponseEntity.ok(propertyService.getMyProperties(landlordId));
    }
}
