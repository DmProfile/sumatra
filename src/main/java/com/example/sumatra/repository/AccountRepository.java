package com.example.sumatra.repository;

import com.example.sumatra.domain.entity.AccountEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.LockModeType;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "account", path = "account")
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {


    @Operation(summary = "Find an existing contact")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a inner join a.user u where u.id IN (:userFrom, :userTo) order by case when u.id = :userFrom then 1 else 2 end, u.id")
    List<AccountEntity> findByTransferParticipants(Long userFrom, Long userTo);
}
