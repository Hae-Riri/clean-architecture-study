package com.example.cleanarchitecturestudy.account.application.port.in;

import com.example.cleanarchitecturestudy.account.domain.Account;
import com.example.cleanarchitecturestudy.account.domain.Money;

public interface GetAccountBalanceQuery {
    Money getAccountBalance(Account.AccountId accountId);
}
