package com.grivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DisputeCreateRequest {
    @NotNull(message = "agreementId is required")
    private Long agreementId;

    @NotBlank(message = "description is required")
    private String description;
}
