package com.grivo.controller;

import com.grivo.dto.DisputeCreateRequest;
import com.grivo.dto.DisputeResponse;
import com.grivo.dto.LandlordResponseRequest;
import com.grivo.service.DisputeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/disputes")
public class DisputeController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @PostMapping
    public ResponseEntity<DisputeResponse> create(@Valid @RequestBody DisputeCreateRequest request,
                                                    @RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(disputeService.createDispute(request, userId));
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<DisputeResponse> respond(@PathVariable Long id,
                                                     @Valid @RequestBody LandlordResponseRequest request) {
        return ResponseEntity.ok(disputeService.addLandlordResponse(id, request.getResponse()));
    }

    @PostMapping("/{id}/generate-report")
    public ResponseEntity<DisputeResponse> generateReport(@PathVariable Long id) {
        return ResponseEntity.ok(disputeService.generateReport(id));
    }

    @GetMapping("/{id}/complaint-letter")
    public ResponseEntity<Map<String, String>> getComplaintLetter(@PathVariable Long id) {
        String letter = disputeService.generateComplaintLetter(id);
        return ResponseEntity.ok(Map.of("letter", letter));
    }

    @GetMapping
    public ResponseEntity<List<DisputeResponse>> getForAgreement(@RequestParam Long agreementId) {
        return ResponseEntity.ok(disputeService.getDisputesForAgreement(agreementId));
    }
}
