package com.grivo.repository;

import com.grivo.entity.Property;
import com.grivo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByLandlord(User landlord);
}
