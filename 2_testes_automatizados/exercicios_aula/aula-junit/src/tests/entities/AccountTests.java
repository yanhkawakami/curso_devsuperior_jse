package tests.entities;

import entities.Account;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import tests.factory.AccountFactory;

public class AccountTests {

    @Test
    public void depositShouldIncreaseBalanceWhenPositiveAmount(){
        Account account = AccountFactory.createEmptyAccount();
        double amount = 200.00;
        double expectedValue = 196.00;

        account.deposit(amount);

        assert account.getBalance().equals(expectedValue);
    }

    @Test
    public void depositShouldNotIncreaseBalanceWhenNegativeAmount(){
        double expectedValue = 100.00;
        Account account = AccountFactory.createAccount(expectedValue);
        double amount = -200.00;

        account.deposit(amount);

        assert account.getBalance().equals(expectedValue);
    }

    @Test
    public void fullWithdrawShouldClearBalanceAndReturnFullBalance(){
        double expectedValue = 0.0;
        double initialBalance = 800.0;
        Account account = AccountFactory.createAccount(initialBalance);

        Double result = account.fullWithdraw();

        assert account.getBalance().equals(expectedValue);
        assert result.equals(initialBalance);
    }

    @Test
    public void withdrawShouldDecreaseBalanceWhenSufficientBalance(){
        Account account = AccountFactory.createAccount(800.0);

        account.withdraw(500.0);

        assert account.getBalance().equals(300.0);
    }

    @Test
    public void withdrawShouldThrowExceptionWhenInsufficientBalance(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Account account = AccountFactory.createAccount(800.0);
            account.withdraw(801.0);
        });
    }
}
