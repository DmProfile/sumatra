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

@Component
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;

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

        updateBalance(transferItem, transferParticipants);
        accountRepository.saveAllAndFlush(transferParticipants);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public void updateAccounts(TransferItem transferItem) {
        accountRepository.transferMoneyBetweenUsers(transferItem.userFrom(), transferItem.userTo(), transferItem.amount());
    }

    @Transactional
    public void doTransfer(TransferItem transferItem) {
        accountRepository.transferMoneyBetweenUsersWithAdvisoryLock(transferItem.userFrom(), transferItem.userTo(), transferItem.amount());
    }

    private void updateBalance(TransferItem transferItem, List<AccountEntity> transferParticipants) {
        AccountEntity accountFrom = transferParticipants.get(0);
        BigDecimal balance = accountFrom.getBalance();

        if (balance.compareTo(transferItem.amount()) < 0) {
            throw new RuntimeException("Недостаточно средств на счете");
        }

        balance = balance.subtract(transferItem.amount());
        accountFrom.setBalance(balance);

        AccountEntity accountTo = transferParticipants.get(1);
        balance = accountTo.getBalance();
        balance = balance.add(transferItem.amount());
        accountTo.setBalance(balance);
    }
}
