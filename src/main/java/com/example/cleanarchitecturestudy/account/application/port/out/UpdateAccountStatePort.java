package com.example.cleanarchitecturestudy.account.application.port.out;

import com.example.cleanarchitecturestudy.account.domain.Account;

public interface UpdateAccountStatePort {
    void updateActivities(Account account);
}
