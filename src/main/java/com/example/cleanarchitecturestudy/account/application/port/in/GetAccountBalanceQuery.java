package com.example.cleanarchitecturestudy.account.application.port.in;

import com.example.cleanarchitecturestudy.account.domain.Account;

public interface GetAccountBalanceQuery {
    Money getAccountBalance(Account.AccountId accountId);
}
