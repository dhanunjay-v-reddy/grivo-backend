package com.grivo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PropertyCreateRequest {
    @NotBlank(message = "addressLine is required")
    private String addressLine;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "state is required")
    private String state;

    private String pincode;
}
