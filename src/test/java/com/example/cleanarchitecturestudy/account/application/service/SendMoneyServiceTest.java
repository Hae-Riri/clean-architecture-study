package com.example.cleanarchitecturestudy.account.application.service;

import com.example.cleanarchitecturestudy.account.application.port.in.SendMoneyCommand;
import com.example.cleanarchitecturestudy.account.application.port.out.AccountLock;
import com.example.cleanarchitecturestudy.account.application.port.out.LoadAccountPort;
import com.example.cleanarchitecturestudy.account.application.port.out.UpdateAccountStatePort;
import com.example.cleanarchitecturestudy.account.domain.Account;
import com.example.cleanarchitecturestudy.account.domain.Money;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class SendMoneyServiceTest {

    private final LoadAccountPort loadAccountPort = Mockito.mock(LoadAccountPort.class);
    private final AccountLock accountLock = Mockito.mock(AccountLock.class);
    private final UpdateAccountStatePort updateAccountStatePort = Mockito.mock(UpdateAccountStatePort.class);

    private final SendMoneyService sendMoneyService = new SendMoneyService(loadAccountPort, accountLock, updateAccountStatePort, moneyTransferProperties());

    @Test
    void givenWithdrawalFails_thenOnlySourceAccountIsLockedAndReleased() {
        Account.AccountId sourceAccountId = new Account.AccountId(41L);
        Account sourceAccount = givenAnAccountWithId(sourceAccountId);

        Account.AccountId targetAccountId = new Account.AccountId(42L);
        Account targetAccount = givenAnAccountWithId(targetAccountId);

        // 출금될 곳은 실패, 입금될 곳은 성공
        givenWithdrawalWillFail(sourceAccount);
        givenDepositWillSucceed(targetAccount);

        SendMoneyCommand command = new SendMoneyCommand(sourceAccountId, targetAccountId, Money.of(300L));

        boolean success = sendMoneyService.sendMoney(command);
        assertThat(success).isFalse();

        then(accountLock).should().lockAccount(eq(sourceAccountId)); // source에 대한 loadAccount 메소드가 실행되어야 함.
        then(accountLock).should().releaseAccount(eq(sourceAccountId)); //source에 대한 releaseAccount 메소드가 실행되어야 함.
        then(accountLock).should(times(0)).lockAccount(eq(targetAccountId)); // target에 대한 loadAccount 메소드가 실행되면 안됨.
    }

    @Test
    void transactionsSucceed() {
        Account sourceAccount = givenSourceAccount();
        Account targetAccount = givenTargetAccount();

        givenWithdrawalWillSucceed(sourceAccount);
        givenDepositWillSucceed(targetAccount);

        Money money = Money.of(500L);

        SendMoneyCommand command = new SendMoneyCommand(sourceAccount.getId().get(), targetAccount.getId().get(), money);
        boolean success = sendMoneyService.sendMoney(command);
        assertThat(success).isTrue();

        Account.AccountId sourceAccountId = sourceAccount.getId().get();
        Account.AccountId targetAccountId = targetAccount.getId().get();

        // 1. 출금될 계좌를 lock하는 메소드가 실행되었어야 함.
        then(accountLock).should().lockAccount(eq(sourceAccountId));
        // 2. 출금될 계좌에 대한 withdraw 메소드가 실행되었어야 함.
        then(sourceAccount).should().withdraw(eq(money), eq(targetAccountId));
        // 3. 출금될 계좌를 release하는 메소드가 실행되었어야 함.
        then(accountLock).should().releaseAccount(eq(sourceAccountId));

        // 4. 입금될 계좌를 lock 하는 메소드가 실행되었어야 함.
        then(accountLock).should().lockAccount(eq(targetAccountId));
        // 5. 입금될 계좌에 대한 deposit 메소드가 실행되었어야 함.
        then(targetAccount).should().deposit(eq(money), eq(sourceAccountId));
        // 6. 입금될 계좌를 release하는 메소드가 실행되었어야 함.
        then(accountLock).should().releaseAccount(eq(targetAccountId));

        thenAccountsHaveBeenUpdated(sourceAccountId, targetAccountId);
    }

    private void thenAccountsHaveBeenUpdated(Account.AccountId... accountIds) {
        // ArgumentCaptor : mockito가 호출된 메소드에 어떤 인자가 들어갔는지 캡처하는 용도
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        // updateAccountStatePort의 updateActivities 메소드가 accountId 개수만큼 실행되었어야 한다는 것 같다. ?
        then(updateAccountStatePort).should(times(accountIds.length))
                .updateActivities(accountCaptor.capture());

        List<Account.AccountId> updatedAccountIds = accountCaptor.getAllValues()
                .stream()
                .map(Account::getId)
                .map(Optional::get)
                .collect(Collectors.toList());

        for (Account.AccountId accountId : accountIds) {
            assertThat(updatedAccountIds).contains(accountId);
        }
    }

    private void givenWithdrawalWillSucceed(Account account) {
        given(account.withdraw(any(Money.class), any(Account.AccountId.class)))
                .willReturn(true);
    }

    private Account givenTargetAccount() {
        return givenAnAccountWithId(new Account.AccountId(42L));
    }

    private Account givenSourceAccount() {
        return givenAnAccountWithId(new Account.AccountId(41L));
    }

    private void givenDepositWillSucceed(Account account) {
        given(account.deposit(any(Money.class), any(Account.AccountId.class)))
                .willReturn(true);
    }

    private void givenWithdrawalWillFail(Account account) {
        given(account.withdraw(any(Money.class), any(Account.AccountId.class)))
                .willReturn(false);
    }

    private Account givenAnAccountWithId(Account.AccountId id) {
        Account account = Mockito.mock(Account.class);
        given(account.getId())
                .willReturn(Optional.of(id));
        given(loadAccountPort.loadAccount(eq(account.getId().get()), any(LocalDateTime.class)))
                .willReturn(account);
        return account;
    }

    private MoneyTransferProperties moneyTransferProperties() {
        return new MoneyTransferProperties(Money.of(Long.MAX_VALUE));
    }
}