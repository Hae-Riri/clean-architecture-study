package com.example.cleanarchitecturestudy.account.application.port.out;

import com.example.cleanarchitecturestudy.account.domain.Account;

import java.time.LocalDateTime;

public interface LoadAccountPort {
    Account loadAccount(Account.AccountId accountId, LocalDateTime baselineDate);
}
