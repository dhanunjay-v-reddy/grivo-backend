package com.grivo.controller;

import com.grivo.dto.AgreementCreateRequest;
import com.grivo.dto.AgreementResponse;
import com.grivo.service.AgreementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agreements")
public class AgreementController {

    private final AgreementService agreementService;

    public AgreementController(AgreementService agreementService) {
        this.agreementService = agreementService;
    }

    @PostMapping
    public ResponseEntity<AgreementResponse> create(@Valid @RequestBody AgreementCreateRequest request,
                                                      @RequestParam Long landlordId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agreementService.createAgreement(request, landlordId));
    }

    @GetMapping
    public ResponseEntity<List<AgreementResponse>> getMine(@RequestParam Long userId) {
        return ResponseEntity.ok(agreementService.getMyAgreements(userId));
    }
}
