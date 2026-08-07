package com.grivo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LandlordResponseRequest {
    @NotBlank(message = "response is required")
    private String response;
}
