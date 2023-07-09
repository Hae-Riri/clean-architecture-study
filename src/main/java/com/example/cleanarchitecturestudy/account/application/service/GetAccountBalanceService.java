package com.example.cleanarchitecturestudy.account.application.service;

import com.example.cleanarchitecturestudy.account.application.port.in.GetAccountBalanceQuery;
import com.example.cleanarchitecturestudy.account.domain.Account;
import com.example.cleanarchitecturestudy.account.domain.Money;

import java.time.LocalDateTime;

public class GetAccountBalanceService implements GetAccountBalanceQuery {
    private final LoadAccontPort loadAccontPort;

    @Override
    public Money getAccountBalance(Account.AccountId accountId) {
        return loadAccontPort.loadAccount(accountId, LocalDateTime.now())
                .calculateBalance();
    }
}
