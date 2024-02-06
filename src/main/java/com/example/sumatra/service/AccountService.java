package com.example.sumatra.service;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.example.sumatra.Tables.ACCOUNT;

@Component
@RequiredArgsConstructor
public class AccountService {

    private static final BigDecimal multiplier = BigDecimal.valueOf(207).divide(BigDecimal.valueOf(110), 2, RoundingMode.HALF_UP);
    private static final BigDecimal adder = BigDecimal.valueOf(10).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    private final DSLContext dslContext;

    public void updateAccountsBalance(CountDownLatch latch) {

        dslContext.transaction(configuration -> {
                    List<Long> fetched = dslContext.select(ACCOUNT.ID)
                            .from(ACCOUNT)
                            .where(ACCOUNT.BALANCE.lt(
                                    ACCOUNT.INIT_BALANCE.mul(multiplier)

                            ))
                            .forUpdate()
                            .fetchInto(long.class);

                    dslContext.update(ACCOUNT)
                            .set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.add(ACCOUNT.BALANCE.mul(adder)))
                            .where(ACCOUNT.ID.in(fetched))
                            .execute();
                }

        );

        latch.countDown();
    }
}
