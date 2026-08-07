package com.grivo.entity;

import com.grivo.enums.PhotoType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A single piece of photo evidence. The actual image lives in Cloudinary;
 * this row stores the URL plus a SHA-256 hash of the image bytes computed
 * at upload time, so the evidence is provably unaltered — same
 * tamper-evidence principle used in Plaryu, applied to rental disputes.
 */
@Entity
@Table(name = "inspection_photo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", nullable = false)
    private Agreement agreement;

    @Column(nullable = false)
    private String roomLabel; // e.g. "Living Room", "Kitchen", "Bathroom"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhotoType type;

    @Column(nullable = false)
    private String imageUrl; // Cloudinary secure_url

    @Column(nullable = false)
    private String imageHash; // SHA-256 of the uploaded image bytes

    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime capturedAt;

    @PrePersist
    protected void onCreate() {
        if (this.capturedAt == null) {
            this.capturedAt = LocalDateTime.now();
        }
    }
}
