package com.grivo.controller;

import com.grivo.dto.PhotoUploadResponse;
import com.grivo.enums.PhotoType;
import com.grivo.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PhotoUploadResponse> upload(
            @RequestParam Long agreementId,
            @RequestParam String roomLabel,
            @RequestParam PhotoType type,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam MultipartFile file) {
        PhotoUploadResponse response = photoService.uploadPhoto(agreementId, roomLabel, type, latitude, longitude, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PhotoUploadResponse>> getPhotos(
            @RequestParam Long agreementId,
            @RequestParam(required = false) PhotoType type) {
        return ResponseEntity.ok(photoService.getPhotos(agreementId, type));
    }
}
