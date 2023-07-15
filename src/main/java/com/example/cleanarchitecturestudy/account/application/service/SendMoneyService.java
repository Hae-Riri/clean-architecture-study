package com.example.cleanarchitecturestudy.account.application.service;

import com.example.cleanarchitecturestudy.account.application.port.in.SendMoneyCommand;
import com.example.cleanarchitecturestudy.account.application.port.in.SendMoneyUseCase;
import com.example.cleanarchitecturestudy.account.application.port.out.AccountLock;
import com.example.cleanarchitecturestudy.account.application.port.out.LoadAccountPort;
import com.example.cleanarchitecturestudy.account.application.port.out.UpdateAccountStatePort;
import com.example.cleanarchitecturestudy.account.domain.Account;
import com.example.cleanarchitecturestudy.common.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@UseCase
@Transactional
public class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final AccountLock accountLock;
    private final UpdateAccountStatePort updateAccountStatePort;
    private final MoneyTransferProperties moneyTransferProperties;

    @Override
    public boolean sendMoney(SendMoneyCommand command) {
        checkThreshold(command);

        LocalDateTime baselineDate = LocalDateTime.now().minusDays(10);

        Account sourceAccount = loadAccountPort.loadAccount(command.getSourceAccountId(), baselineDate);
        Account targetAccount = loadAccountPort.loadAccount(command.getTargetAccountId(), baselineDate);

        Account.AccountId sourceAccountId = sourceAccount.getId().orElseThrow(() -> new IllegalStateException("expected source account ID not to be empty"));
        Account.AccountId targetAccountId = targetAccount.getId().orElseThrow(() -> new IllegalStateException("expected target account ID not to be empty"));

        // 출금을 위한 lock. 실패하면 release 하고 false 반환. source가 출금될 계좌이고 target이 송금될 계좌이므로 일단 source부터 lock?
        accountLock.lockAccount(sourceAccountId);
        if(!sourceAccount.withdraw(command.getMoney(), targetAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            return false;
        }

        // 입금을 위한 lock. 실패하면 release 하고 false 반환. source에 대한 출금 처리 끝났으면 target에 입금하기 위한 lock.
        // 여기서 실패하면 출금처와 입금처 모두를 release 해야함.
        accountLock.lockAccount(targetAccountId);
        if(!targetAccount.deposit(command.getMoney(), sourceAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            accountLock.releaseAccount(targetAccountId);
            return false;
        }

        // 다 성공해서 넘어왔다면 방금 withdraw와 deposit으로 만들어진 activity들의 id가 null로 들어갔을테니 이제야 세팅.
        updateAccountStatePort.updateActivities(sourceAccount);
        updateAccountStatePort.updateActivities(targetAccount);

        // 다 성공해서 넘어왔다면 출금처와 입금처 모두 release
        accountLock.releaseAccount(sourceAccountId);
        accountLock.releaseAccount(targetAccountId);

        return true;
    }

    private void checkThreshold(SendMoneyCommand command) {
        if(command.getMoney().isGreaterThan(moneyTransferProperties.getMaximumTransferThreshold())) {
            throw new ThresholdExceededException(moneyTransferProperties.getMaximumTransferThreshold(), command.getMoney());
        }
    }
}
