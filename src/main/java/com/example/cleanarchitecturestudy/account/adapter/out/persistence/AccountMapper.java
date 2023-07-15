package com.example.cleanarchitecturestudy.account.adapter.out.persistence;

import com.example.cleanarchitecturestudy.account.domain.Account;
import com.example.cleanarchitecturestudy.account.domain.Activity;
import com.example.cleanarchitecturestudy.account.domain.ActivityWindow;
import com.example.cleanarchitecturestudy.account.domain.Money;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountMapper {

    Account mapToDomainEntity(AccountJpaEntity account, List<ActivityJpaEntity> activities, Long withdrawalBalance, Long depositBalance) {
        Money baselineBalance = Money.subtract(Money.of(depositBalance), Money.of(withdrawalBalance)); // 잔액 = 예금액 - 출금액
        return Account.withId(new Account.AccountId(account.getId()), baselineBalance, mapToActivityWindow(activities));
    }

    ActivityWindow mapToActivityWindow(List<ActivityJpaEntity> activities) {
        List<Activity> mappedActivities = new ArrayList<>();

        for (ActivityJpaEntity activity : activities) {
            mappedActivities.add(new Activity(
                    new Account.AccountId(activity.getOwnerAccountId()),
                    new Account.AccountId(activity.getSourceAccountId()),
                    new Account.AccountId(activity.getTargetAccountId()),
                    activity.getTimestamp(),
                    Money.of(activity.getAmount())
            ));
        }

        return new ActivityWindow(mappedActivities);
    }

    ActivityJpaEntity mapToJpaEntity(Activity activity) {
        return new ActivityJpaEntity(
                activity.getId() == null ? null : activity.getId().getValue(),
                activity.getTimestamp(),
                activity.getOwnerAccountId().getValue(),
                activity.getSourceAccountId().getValue(),
                activity.getTargetAccountId().getValue(),
                activity.getMoney().getAmount().longValue()
        );
    }
}
