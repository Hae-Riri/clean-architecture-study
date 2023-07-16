package com.example.cleanarchitecturestudy.account.adapter.out.persistence;

import com.example.cleanarchitecturestudy.account.domain.Account;
import com.example.cleanarchitecturestudy.account.domain.ActivityWindow;
import com.example.cleanarchitecturestudy.account.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static com.example.cleanarchitecturestudy.common.AccountTestData.defaultAccount;
import static com.example.cleanarchitecturestudy.common.ActivityTestData.defaultActivity;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Spring Data Repository를 포함하여 데이터베이스 접근을 위한 객체 네트워크를 인스턴스화해야한다고 알려주는 역할.
@Import({AccountPersistenceAdapter.class, AccountMapper.class}) // 특정 객체가 이 네트워크에 추가됐다고 표현하는 용도
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter adapterUnderTest;

    @Autowired
    private ActivityRepository activityRepository;

    @Test
    @Sql("AccountPersistenceAdapterTest.sql")
    void loadsAccount() {
        Account account = adapterUnderTest.loadAccount(new Account.AccountId(1L), LocalDateTime.of(2018, 8, 10, 0, 0));

        // 2018년 이전에 insert된 activity 중에서 ownerId가 1L 인 것들
        assertThat(account.getActivityWindow().getActivities()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(500));
    }

    @Test
    void updateActivities() {
        Account account = defaultAccount()
                .withBaselineBalance(Money.of(500L))
                .withActivityWindow(new ActivityWindow(
                        defaultActivity()
                                .withId(null)
                                .withMoney(Money.of(1L))
                                .build()
                ))
                .build();

        adapterUnderTest.updateActivities(account);

        assertThat(activityRepository.count()).isEqualTo(1); // repository에 count 하면 entity 수 알려줌.

        ActivityJpaEntity savedActivity = activityRepository.findAll().get(0);
        assertThat(savedActivity.getAmount()).isEqualTo(1L);
    }
}