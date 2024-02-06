package com.example.sumatra.repository;

import com.example.sumatra.domain.entity.PhoneDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "phone", path = "phone")
public interface PhoneDataRepository extends JpaRepository<PhoneDataEntity, Long> {
}
