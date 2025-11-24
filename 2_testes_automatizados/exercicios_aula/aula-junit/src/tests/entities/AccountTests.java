package tests.entities;

import entities.Account;
import org.junit.Test;

public class AccountTests {

    @Test
    public void depositShouldIncreaseBalanceWhenPositiveAmount(){
        Account account = new Account(1L, 0.0);
        double amount = 200.00;
        double expectedValue = 196.00;

        account.deposit(amount);

        assert account.getBalance().equals(expectedValue);
    }

    @Test
    public void depositShouldNotIncreaseBalanceWhenNegativeAmount(){
        Account account = new Account(1L, 100.0);
        double amount = -200.00;
        double expectedValue = 100.00;

        account.deposit(amount);

        assert account.getBalance().equals(expectedValue);
    }
}
