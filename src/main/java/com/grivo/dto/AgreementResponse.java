package com.grivo.dto;

import com.grivo.entity.Agreement;
import com.grivo.enums.AgreementStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AgreementResponse {
    private Long id;
    private String propertyAddress;
    private String tenantName;
    private String landlordName;
    private Double monthlyRent;
    private Double depositAmount;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private AgreementStatus status;

    public static AgreementResponse fromEntity(Agreement a) {
        return AgreementResponse.builder()
                .id(a.getId())
                .propertyAddress(a.getProperty().getAddressLine() + ", " + a.getProperty().getCity())
                .tenantName(a.getTenant().getName())
                .landlordName(a.getLandlord().getName())
                .monthlyRent(a.getMonthlyRent())
                .depositAmount(a.getDepositAmount())
                .moveInDate(a.getMoveInDate())
                .moveOutDate(a.getMoveOutDate())
                .status(a.getStatus())
                .build();
    }
}
