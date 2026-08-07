package com.grivo.service;

import com.grivo.dto.AgreementCreateRequest;
import com.grivo.dto.AgreementResponse;
import com.grivo.entity.Agreement;
import com.grivo.entity.Property;
import com.grivo.entity.User;
import com.grivo.enums.AgreementStatus;
import com.grivo.enums.Role;
import com.grivo.exception.InvalidRequestException;
import com.grivo.exception.ResourceNotFoundException;
import com.grivo.repository.AgreementRepository;
import com.grivo.repository.PropertyRepository;
import com.grivo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgreementService {

    private final AgreementRepository agreementRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public AgreementService(AgreementRepository agreementRepository, PropertyRepository propertyRepository,
                             UserRepository userRepository) {
        this.agreementRepository = agreementRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    public AgreementResponse createAgreement(AgreementCreateRequest request, Long landlordId) {
        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + landlordId));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + request.getPropertyId()));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new InvalidRequestException("You do not own this property");
        }

        User tenant = userRepository.findByEmail(request.getTenantEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No tenant registered with email: " + request.getTenantEmail()));

        if (tenant.getRole() != Role.TENANT) {
            throw new InvalidRequestException("That email does not belong to a TENANT account");
        }

        Agreement agreement = Agreement.builder()
                .property(property)
                .tenant(tenant)
                .landlord(landlord)
                .monthlyRent(request.getMonthlyRent())
                .depositAmount(request.getDepositAmount())
                .moveInDate(request.getMoveInDate())
                .status(AgreementStatus.ACTIVE)
                .build();
        agreement = agreementRepository.save(agreement);
        return AgreementResponse.fromEntity(agreement);
    }

    public List<AgreementResponse> getMyAgreements(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        List<Agreement> agreements = user.getRole() == Role.LANDLORD
                ? agreementRepository.findByLandlord(user)
                : agreementRepository.findByTenant(user);

        return agreements.stream().map(AgreementResponse::fromEntity).toList();
    }
}
