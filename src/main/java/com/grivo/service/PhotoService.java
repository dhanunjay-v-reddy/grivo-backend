package com.grivo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.grivo.dto.PhotoUploadResponse;
import com.grivo.entity.Agreement;
import com.grivo.entity.InspectionPhoto;
import com.grivo.enums.PhotoType;
import com.grivo.exception.InvalidRequestException;
import com.grivo.exception.ResourceNotFoundException;
import com.grivo.repository.AgreementRepository;
import com.grivo.repository.InspectionPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles evidence photo uploads. Each photo is:
 *  1. Hashed (SHA-256 of the raw bytes) BEFORE upload, so the hash reflects
 *     exactly what was captured.
 *  2. Uploaded to Cloudinary for real, durable storage (unlike Plaryu, which
 *     only stored the hash — here the actual image is retrievable, which
 *     matters for a dispute you might need to show someone).
 *  3. Saved with the hash, so at any point the image can be re-hashed and
 *     compared to prove it hasn't been swapped or edited since capture.
 */
@Service
public class PhotoService {

    private final Cloudinary cloudinary;
    private final AgreementRepository agreementRepository;
    private final InspectionPhotoRepository photoRepository;
    private final HashUtil hashUtil;

    public PhotoService(Cloudinary cloudinary, AgreementRepository agreementRepository,
                         InspectionPhotoRepository photoRepository, HashUtil hashUtil) {
        this.cloudinary = cloudinary;
        this.agreementRepository = agreementRepository;
        this.photoRepository = photoRepository;
        this.hashUtil = hashUtil;
    }

    public PhotoUploadResponse uploadPhoto(Long agreementId, String roomLabel, PhotoType type,
                                            Double latitude, Double longitude, MultipartFile file) {
        Agreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement not found: " + agreementId));

        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("A photo file is required");
        }

        try {
            byte[] bytes = file.getBytes();
            String hash = hashUtil.sha256(bytes);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                    "folder", "grivo/agreement-" + agreementId,
                    "resource_type", "image"
            ));
            String imageUrl = (String) uploadResult.get("secure_url");

            InspectionPhoto photo = InspectionPhoto.builder()
                    .agreement(agreement)
                    .roomLabel(roomLabel)
                    .type(type)
                    .imageUrl(imageUrl)
                    .imageHash(hash)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            photo = photoRepository.save(photo);

            return PhotoUploadResponse.fromEntity(photo);
        } catch (IOException e) {
            throw new InvalidRequestException("Failed to upload photo: " + e.getMessage());
        }
    }

    public List<PhotoUploadResponse> getPhotos(Long agreementId, PhotoType type) {
        Agreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement not found: " + agreementId));

        List<InspectionPhoto> photos = type != null
                ? photoRepository.findByAgreementAndType(agreement, type)
                : photoRepository.findByAgreement(agreement);

        return photos.stream().map(PhotoUploadResponse::fromEntity).toList();
    }
}
