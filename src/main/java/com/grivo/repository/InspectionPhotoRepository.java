package com.grivo.repository;

import com.grivo.entity.Agreement;
import com.grivo.entity.InspectionPhoto;
import com.grivo.enums.PhotoType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionPhotoRepository extends JpaRepository<InspectionPhoto, Long> {
    List<InspectionPhoto> findByAgreementAndType(Agreement agreement, PhotoType type);
    List<InspectionPhoto> findByAgreement(Agreement agreement);
}
