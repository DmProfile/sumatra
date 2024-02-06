package com.example.sumatra.repository;

import com.example.sumatra.domain.entity.EmailDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "account", path = "account")
public interface EmailDataRepository extends JpaRepository<EmailDataEntity, Long> {
}
