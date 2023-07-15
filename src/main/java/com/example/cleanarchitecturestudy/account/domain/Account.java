package com.example.cleanarchitecturestudy.account.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    private final AccountId id;

    public Optional<AccountId> getId() {
        return Optional.ofNullable(this.id);
    }

    @Getter
    private final Money baselineBalance; // activityWindow의 첫 활동 바로 전까지의 잔고.

    @Getter
    private final ActivityWindow activityWindow; // 며칠 또는 몇 주간의 범위에 해당하는 활동을 보유. 입출금 시 활동창에 활동 추가만 함.

    public static Account withoutId(Money baselineBalance, ActivityWindow activityWindow) {
        return new Account(null, baselineBalance, activityWindow);
    }

    public static Account withId(AccountId accountId, Money baselineBalance, ActivityWindow activityWindow) {
        return new Account(accountId, baselineBalance, activityWindow);
    }

    // 현재 잔고 = 활동 직전까지의 잔고 + 현재까지의 활동창 내 모든 활동들의 잔고
    public Money calculateBalance() {
        return Money.add(
                this.baselineBalance,
                this.activityWindow.calculateBalance(this.id));
    }

    public boolean withdraw(Money money, AccountId targetAccountId) {
        if (!mayWithdraw(money)) {
            return false;
        }

        Activity withdrawal = new Activity(
                this.id,
                this.id,
                targetAccountId,
                LocalDateTime.now(),
                money
        );

        this.activityWindow.addActivity(withdrawal);
        return true;
    }

    //출금 가능한지 확인
    private boolean mayWithdraw(Money money) {
        return Money.add(
                this.calculateBalance(),
                money.negate()
        ).isPositive();
    }

    public boolean deposit(Money money, AccountId sourceAccountId) {
        Activity deposit = new Activity(
                this.id,
                sourceAccountId,
                this.id,
                LocalDateTime.now(),
                money
        );
        this.activityWindow.addActivity(deposit);
        return true;
    }

    @Value
    public static class AccountId {
        private Long value;
        // 인텔리제이에서 노티해주는 내용 왈, 이미 @Value가 불변 객체를 보장해주고 priave하게 이 필드를 지켜주니까 private이라는 말을 제거해도 된다는 말임.
    }
}
