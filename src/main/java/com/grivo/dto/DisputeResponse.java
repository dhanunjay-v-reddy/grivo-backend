package com.grivo.dto;

import com.grivo.entity.Dispute;
import com.grivo.enums.DisputeStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DisputeResponse {
    private Long id;
    private Long agreementId;
    private String propertyAddress;
    private String raisedByName;
    private String description;
    private String landlordResponse;
    private DisputeStatus status;
    private String reportUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DisputeResponse fromEntity(Dispute d) {
        return DisputeResponse.builder()
                .id(d.getId())
                .agreementId(d.getAgreement().getId())
                .propertyAddress(d.getAgreement().getProperty().getAddressLine())
                .raisedByName(d.getRaisedBy().getName())
                .description(d.getDescription())
                .landlordResponse(d.getLandlordResponse())
                .status(d.getStatus())
                .reportUrl(d.getReportUrl())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
