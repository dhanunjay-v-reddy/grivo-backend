package com.grivo.service;

import com.grivo.dto.PropertyCreateRequest;
import com.grivo.dto.PropertyResponse;
import com.grivo.entity.Property;
import com.grivo.entity.User;
import com.grivo.exception.ResourceNotFoundException;
import com.grivo.repository.PropertyRepository;
import com.grivo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    public PropertyResponse createProperty(PropertyCreateRequest request, Long landlordId) {
        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + landlordId));

        Property property = Property.builder()
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .landlord(landlord)
                .build();
        property = propertyRepository.save(property);
        return PropertyResponse.fromEntity(property);
    }

    public List<PropertyResponse> getMyProperties(Long landlordId) {
        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + landlordId));
        return propertyRepository.findByLandlord(landlord).stream()
                .map(PropertyResponse::fromEntity)
                .toList();
    }
}
