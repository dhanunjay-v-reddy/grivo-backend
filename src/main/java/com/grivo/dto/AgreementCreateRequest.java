package com.grivo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AgreementCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "tenantEmail is required")
    private String tenantEmail; // landlord invites tenant by email

    @NotNull(message = "monthlyRent is required")
    @Positive
    private Double monthlyRent;

    @NotNull(message = "depositAmount is required")
    @Positive
    private Double depositAmount;

    @NotNull(message = "moveInDate is required")
    private LocalDate moveInDate;
}
