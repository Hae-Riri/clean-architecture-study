package com.example.cleanarchitecturestudy.account.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public class Activity {

    @Getter
    private final ActivityId id;

    @Getter
    @NonNull
    private final Account.AccountId ownerAccountId; // 이 액티비티를 소유한 계정?

    @Getter
    @NonNull
    private final Account.AccountId sourceAccountId; //인출 관련, withdrawal

    @Getter
    @NonNull
    private final Account.AccountId targetAccountId; //예금 관련. deposit

    @Getter
    @NonNull
    private final LocalDateTime timestamp;

    @Getter
    @NonNull
    private final Money money;

    public Activity(
            @NonNull Account.AccountId ownerAccountId,
            @NonNull Account.AccountId sourceAccountId,
            @NonNull Account.AccountId targetAccountId,
            @NonNull LocalDateTime timestamp,
            @NonNull Money money) {
        this.id = null;
        this.ownerAccountId = ownerAccountId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.timestamp = timestamp;
        this.money = money;
    }

    @RequiredArgsConstructor
    public static class ActivityId {
        private final Long value;
    }
}
