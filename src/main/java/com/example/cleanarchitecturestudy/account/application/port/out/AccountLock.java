package com.example.cleanarchitecturestudy.account.application.port.out;

import com.example.cleanarchitecturestudy.account.domain.Account;

public interface AccountLock {
    void lockAccount(Account.AccountId accountId);
    void releaseAccount(Account.AccountId accountId);
}
