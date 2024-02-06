package com.example.sumatra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final Lock taskLock;
    private final AccountService accountService;

    @Scheduled(cron = "${app.cron}")
    public void scheduledTask() {
        log.info("Scheduled task started");
        CountDownLatch latch = new CountDownLatch(1);
        if (taskLock.tryLock()) {
            try {
                accountService.updateAccountsBalance(latch);
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                taskLock.unlock();
                log.info("Scheduled task stopped");
            }
        } else {
            log.info("The previous task is still running. Skipping this schedule.");
        }
    }
}
