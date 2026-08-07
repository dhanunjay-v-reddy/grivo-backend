package com.grivo.dto;

import com.grivo.entity.Property;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PropertyResponse {
    private Long id;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private String landlordName;

    public static PropertyResponse fromEntity(Property p) {
        return PropertyResponse.builder()
                .id(p.getId())
                .addressLine(p.getAddressLine())
                .city(p.getCity())
                .state(p.getState())
                .pincode(p.getPincode())
                .landlordName(p.getLandlord().getName())
                .build();
    }
}
