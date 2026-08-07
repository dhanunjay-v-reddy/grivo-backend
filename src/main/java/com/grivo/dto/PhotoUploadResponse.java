package com.grivo.dto;

import com.grivo.entity.InspectionPhoto;
import com.grivo.enums.PhotoType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PhotoUploadResponse {
    private Long id;
    private String roomLabel;
    private PhotoType type;
    private String imageUrl;
    private String imageHash;
    private Double latitude;
    private Double longitude;
    private LocalDateTime capturedAt;

    public static PhotoUploadResponse fromEntity(InspectionPhoto p) {
        return PhotoUploadResponse.builder()
                .id(p.getId())
                .roomLabel(p.getRoomLabel())
                .type(p.getType())
                .imageUrl(p.getImageUrl())
                .imageHash(p.getImageHash())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .capturedAt(p.getCapturedAt())
                .build();
    }
}
