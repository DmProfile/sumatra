package com.example.sumatra.service;

import com.example.sumatra.domain.entity.AccountEntity;
import com.example.sumatra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final ConcurrentHashMap<String, Lock> transferLocks = new ConcurrentHashMap<>();

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public void transfer(TransferItem transferItem) {
        long userFrom = transferItem.userFrom();
        long userTo = transferItem.userTo();

        if (userFrom == userTo) {
            throw new RuntimeException("нельзя перевести на себя");
        }

        List<AccountEntity> transferParticipants = accountRepository.findByTransferParticipants(userFrom, userTo);

        if (transferParticipants.size() != 2) {
            throw new RuntimeException("указаны неверные участники перевода");
        }

        Consumer<TransferItem> transferItemConsumer = transfer -> doTransfer(transfer, transferParticipants);

        transferWithLock(transferItem, transferItemConsumer);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public void updateAccounts(TransferItem transferItem) {
        Consumer<TransferItem> transferItemConsumer = transfer -> accountRepository.transferMoneyBetweenUsers(transfer.userFrom(), transfer.userTo(), transfer.amount());
        transferWithLock(transferItem, transferItemConsumer);
    }

    @Transactional
    public void doTransfer(TransferItem transferItem) {
        Consumer<TransferItem> transferItemConsumer = transfer -> accountRepository.transferMoneyBetweenUsersWithAdvisoryLock(transfer.userFrom(), transfer.userTo(), transfer.amount());
        transferWithLock(transferItem, transferItemConsumer);
    }

    private void doTransfer(TransferItem transfer, List<AccountEntity> transferParticipants) {
        updateBalance(transfer, transferParticipants);
        accountRepository.saveAllAndFlush(transferParticipants);
    }

    private void transferWithLock(TransferItem transferItem, Consumer<TransferItem> transferItemConsumer) {
        String lockKey = getLockKey(transferItem.userFrom(), transferItem.userTo());
        Lock transferLock = transferLocks.computeIfAbsent(lockKey, k -> new ReentrantLock());

        boolean lockAcquired = false;

        try {
            lockAcquired = transferLock.tryLock(5, TimeUnit.SECONDS);
            if (lockAcquired) {
                transferItemConsumer.accept(transferItem);
            } else {
                throw new RuntimeException("Время ожидания операции перевода истекло");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Операция перевода прервана");
        } finally {
            if (lockAcquired) {
                transferLock.unlock();
                transferLocks.remove(lockKey);
            }
        }
    }

    private void updateBalance(TransferItem transferItem, List<AccountEntity> transferParticipants) {
        AccountEntity accountFrom = transferParticipants.get(0);
        BigDecimal balance = accountFrom.getBalance();

        if (balance.compareTo(transferItem.amount()) < 0) {
            throw new RuntimeException("Недостаточно средств на счете");
        }

        BigDecimal subtract = balance.subtract(transferItem.amount());
        accountFrom.setBalance(subtract);

        AccountEntity accountTo = transferParticipants.get(1);
        BigDecimal accountToBalance = accountTo.getBalance();
        BigDecimal added = accountToBalance.add(transferItem.amount());
        accountTo.setBalance(added);
    }

    private String getLockKey(long userFrom, long userTo) {
        return Math.min(userFrom, userTo) + "-" + Math.max(userFrom, userTo);
    }
}
