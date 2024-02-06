package com.example.sumatra.service;

import java.math.BigDecimal;

public record TransferItem(Long userFrom, Long userTo, BigDecimal amount) {
}
