package com.grivo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.grivo.dto.DisputeCreateRequest;
import com.grivo.dto.DisputeResponse;
import com.grivo.entity.Agreement;
import com.grivo.entity.Dispute;
import com.grivo.entity.InspectionPhoto;
import com.grivo.entity.User;
import com.grivo.enums.AgreementStatus;
import com.grivo.enums.DisputeStatus;
import com.grivo.enums.PhotoType;
import com.grivo.exception.ResourceNotFoundException;
import com.grivo.repository.AgreementRepository;
import com.grivo.repository.DisputeRepository;
import com.grivo.repository.InspectionPhotoRepository;
import com.grivo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final AgreementRepository agreementRepository;
    private final UserRepository userRepository;
    private final InspectionPhotoRepository photoRepository;
    private final PdfReportService pdfReportService;
    private final ComplaintTemplateService complaintTemplateService;
    private final Cloudinary cloudinary;

    public DisputeService(DisputeRepository disputeRepository, AgreementRepository agreementRepository,
                          UserRepository userRepository, InspectionPhotoRepository photoRepository,
                          PdfReportService pdfReportService, ComplaintTemplateService complaintTemplateService,
                          Cloudinary cloudinary) {
        this.disputeRepository = disputeRepository;
        this.agreementRepository = agreementRepository;
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.pdfReportService = pdfReportService;
        this.complaintTemplateService = complaintTemplateService;
        this.cloudinary = cloudinary;
    }

    @Transactional
    public DisputeResponse createDispute(DisputeCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Agreement agreement = agreementRepository.findById(request.getAgreementId())
                .orElseThrow(() -> new ResourceNotFoundException("Agreement not found: " + request.getAgreementId()));

        Dispute dispute = Dispute.builder()
                .agreement(agreement)
                .raisedBy(user)
                .description(request.getDescription())
                .status(DisputeStatus.OPEN)
                .build();
        dispute = disputeRepository.save(dispute);

        agreement.setStatus(AgreementStatus.DISPUTED);
        agreementRepository.save(agreement);

        return DisputeResponse.fromEntity(dispute);
    }

    public DisputeResponse addLandlordResponse(Long disputeId, String response) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found: " + disputeId));
        dispute.setLandlordResponse(response);
        dispute.setStatus(DisputeStatus.LANDLORD_RESPONDED);
        dispute = disputeRepository.save(dispute);
        return DisputeResponse.fromEntity(dispute);
    }

    @Transactional
    public DisputeResponse generateReport(Long disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found: " + disputeId));

        List<InspectionPhoto> moveIn = photoRepository.findByAgreementAndType(dispute.getAgreement(), PhotoType.MOVE_IN);
        List<InspectionPhoto> moveOut = photoRepository.findByAgreementAndType(dispute.getAgreement(), PhotoType.MOVE_OUT);

        byte[] pdfBytes = pdfReportService.generateReport(dispute, moveIn, moveOut);

        String reportUrl;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(pdfBytes, ObjectUtils.asMap(
                    "folder", "grivo/reports",
                    "resource_type", "raw",
                    "public_id", "dispute-" + disputeId + "-report"
            ));
            reportUrl = (String) uploadResult.get("secure_url");
        } catch (java.io.IOException e) {
            throw new com.grivo.exception.InvalidRequestException("Failed to upload report: " + e.getMessage());
        }

        dispute.setReportUrl(reportUrl);
        dispute = disputeRepository.save(dispute);

        return DisputeResponse.fromEntity(dispute);
    }

    public String generateComplaintLetter(Long disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found: " + disputeId));

        dispute.setStatus(DisputeStatus.ESCALATED);
        disputeRepository.save(dispute);

        return complaintTemplateService.generateComplaintLetter(dispute.getAgreement(), dispute.getDescription());
    }

    public List<DisputeResponse> getDisputesForAgreement(Long agreementId) {
        Agreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement not found: " + agreementId));
        return disputeRepository.findAll().stream()
                .filter(d -> d.getAgreement().getId().equals(agreement.getId()))
                .map(DisputeResponse::fromEntity)
                .toList();
    }
}