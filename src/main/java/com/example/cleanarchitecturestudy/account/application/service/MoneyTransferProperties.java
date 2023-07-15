package com.example.cleanarchitecturestudy.account.application.service;

import com.example.cleanarchitecturestudy.account.domain.Money;
import lombok.Getter;

@Getter
public class MoneyTransferProperties {
    private Money maximumTransferThreshold = Money.of(1_000_000L);
}
