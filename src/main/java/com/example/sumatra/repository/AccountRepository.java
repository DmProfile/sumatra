package com.example.sumatra.repository;

import com.example.sumatra.domain.entity.AccountEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "account", path = "account")
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Operation(summary = "Find an existing contact")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a inner join a.user u where u.id IN (:userFrom, :userTo) order by case when u.id = :userFrom then 1 else 2 end, u.id")
    List<AccountEntity> findByTransferParticipants(Long userFrom, Long userTo);

    @Modifying
    @Query("""
            UPDATE AccountEntity a
            SET a.balance =
              CASE
                WHEN a.user.id = :userFrom AND a.balance >= :amount THEN FUNCTION('SUBTRACT', a.balance, :amount)
                ELSE
                  CASE
                    WHEN a.user.id = :userTo THEN FUNCTION('ADD', a.balance, :amount)
                    ELSE a.balance
                  END
              END
            WHERE a.user.id IN (:userFrom, :userTo)
            """)
    int transferMoneyBetweenUsers(@Param("userFrom") Long userFrom, @Param("userTo") Long userTo, @Param("amount") BigDecimal amount);

    @Modifying
    @Query(nativeQuery = true, value =
            "DO $$ " +
                    "BEGIN " +
                    "   PERFORM pg_advisory_xact_lock(:userFrom); " +
                    "   PERFORM pg_advisory_xact_lock(:userTo);" +
                    "   UPDATE account " +
                    "   SET balance = " +
                    "       CASE " +
                    "           WHEN user_id = :userFrom AND balance >= :amount THEN balance - :amount " +
                    "           ELSE " +
                    "               CASE " +
                    "                   WHEN user_id = :userTo THEN balance + :amount " +
                    "                   ELSE balance " +
                    "               END " +
                    "       END " +
                    "   WHERE user_id IN (:userFrom, :userTo); " +
                    "END $$")
    int transferMoneyBetweenUsersWithAdvisoryLock
            (@Param("userFrom") Long userFrom, @Param("userTo") Long userTo, @Param("amount") BigDecimal amount);
}
