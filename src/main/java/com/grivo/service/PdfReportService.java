package com.grivo.service;

import com.grivo.entity.Agreement;
import com.grivo.entity.Dispute;
import com.grivo.entity.InspectionPhoto;
import com.grivo.enums.PhotoType;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds the dispute evidence PDF: agreement summary, dispute description,
 * and move-in vs move-out photos side by side with their hashes printed
 * underneath — so the PDF itself carries proof the images are the exact
 * ones hashed at capture time, not swapped afterward.
 */
@Service
public class PdfReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public byte[] generateReport(Dispute dispute, List<InspectionPhoto> moveInPhotos, List<InspectionPhoto> moveOutPhotos) {
        Agreement agreement = dispute.getAgreement();
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font headingFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

            document.add(new Paragraph("Grivo — Deposit Dispute Evidence Report", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Agreement Summary", headingFont));
            document.add(new Paragraph("Property: " + agreement.getProperty().getAddressLine()
                    + ", " + agreement.getProperty().getCity() + ", " + agreement.getProperty().getState(), normalFont));
            document.add(new Paragraph("Tenant: " + agreement.getTenant().getName(), normalFont));
            document.add(new Paragraph("Landlord: " + agreement.getLandlord().getName(), normalFont));
            document.add(new Paragraph("Deposit Amount: Rs. " + agreement.getDepositAmount(), normalFont));
            document.add(new Paragraph("Move-in Date: " + agreement.getMoveInDate(), normalFont));
            document.add(new Paragraph("Move-out Date: " + (agreement.getMoveOutDate() != null ? agreement.getMoveOutDate() : "N/A"), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Dispute Description", headingFont));
            document.add(new Paragraph(dispute.getDescription(), normalFont));
            if (dispute.getLandlordResponse() != null) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Landlord's Response:", headingFont));
                document.add(new Paragraph(dispute.getLandlordResponse(), normalFont));
            }
            document.add(new Paragraph(" "));

            addPhotoSection(document, "Move-In Evidence", moveInPhotos, headingFont, normalFont, smallFont);
            addPhotoSection(document, "Move-Out Evidence", moveOutPhotos, headingFont, normalFont, smallFont);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "Every photo above is recorded with a SHA-256 hash computed at the moment of capture. "
                    + "If any image were altered after upload, re-hashing it would no longer match the value shown, "
                    + "making tampering detectable.", smallFont));

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    private void addPhotoSection(Document document, String title, List<InspectionPhoto> photos,
                                  Font headingFont, Font normalFont, Font smallFont) throws DocumentException {
        document.add(new Paragraph(title, headingFont));
        if (photos.isEmpty()) {
            document.add(new Paragraph("No photos recorded.", normalFont));
            return;
        }
        for (InspectionPhoto photo : photos) {
            document.add(new Paragraph(photo.getRoomLabel() + " — captured " + photo.getCapturedAt().format(DATE_FMT), normalFont));
            try {
                Image image = Image.getInstance(new URL(photo.getImageUrl()));
                image.scaleToFit(300, 300);
                document.add(image);
            } catch (IOException | BadElementException e) {
                document.add(new Paragraph("[image could not be embedded: " + photo.getImageUrl() + "]", smallFont));
            }
            document.add(new Paragraph("SHA-256: " + photo.getImageHash(), smallFont));
            if (photo.getLatitude() != null && photo.getLongitude() != null) {
                document.add(new Paragraph("Location: " + photo.getLatitude() + ", " + photo.getLongitude(), smallFont));
            }
            document.add(new Paragraph(" "));
        }
    }
}
