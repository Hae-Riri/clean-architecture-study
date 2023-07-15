package com.example.cleanarchitecturestudy.account.application.service;

import com.example.cleanarchitecturestudy.account.application.port.in.GetAccountBalanceQuery;
import com.example.cleanarchitecturestudy.account.application.port.out.LoadAccountPort;
import com.example.cleanarchitecturestudy.account.domain.Account;
import com.example.cleanarchitecturestudy.account.domain.Money;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class GetAccountBalanceService implements GetAccountBalanceQuery {
    private final LoadAccountPort loadAccountPort;

    @Override
    public Money getAccountBalance(Account.AccountId accountId) {
        return loadAccountPort.loadAccount(accountId, LocalDateTime.now())
                .calculateBalance();
    }
}
