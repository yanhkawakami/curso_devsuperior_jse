package tests.entities;

import entities.Account;
import org.junit.Test;
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
}
