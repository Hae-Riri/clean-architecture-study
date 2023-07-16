package com.example.cleanarchitecturestudy.account.application.service;

import com.example.cleanarchitecturestudy.account.domain.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Configuration properties for money transfer use cases.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoneyTransferProperties {

    private Money maximumTransferThreshold = Money.of(1_000_000L);

}
