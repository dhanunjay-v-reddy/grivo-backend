package com.grivo.repository;

import com.grivo.entity.Agreement;
import com.grivo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    List<Agreement> findByTenant(User tenant);
    List<Agreement> findByLandlord(User landlord);
}
