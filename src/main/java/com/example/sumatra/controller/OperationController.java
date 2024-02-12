package com.example.sumatra.controller;

import com.example.sumatra.service.TransferItem;
import com.example.sumatra.service.TransferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Щперации по счетам", description = "API для работы со счетами")
public class OperationController {

    private final TransferService transferService;


    @PostMapping(value = "/users/{id}/account/withdraw", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> withDraw(@PathVariable(value = "id") @Secured Long userId, @RequestBody WithDrawDto withDrawDto) {
        TransferItem transferItem = new TransferItem(userId, withDrawDto.targetUserId, withDrawDto.amount);
        transferService.transfer(transferItem);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/users/{id}/account/withdrawS", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> withDrawS(@PathVariable(value = "id") @Secured Long userId, @RequestBody WithDrawDto withDrawDto) {
        TransferItem transferItem = new TransferItem(userId, withDrawDto.targetUserId, withDrawDto.amount);
        transferService.updateAccounts(transferItem);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/users/{id}/account/withdrawI", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> withDrawI(@PathVariable(value = "id") @Secured Long userId, @RequestBody WithDrawDto withDrawDto) {
        TransferItem transferItem = new TransferItem(userId, withDrawDto.targetUserId, withDrawDto.amount);
        transferService.doTransfer(transferItem);
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    public static class WithDrawDto {
        private Long targetUserId;
        private BigDecimal amount;

    }
}
