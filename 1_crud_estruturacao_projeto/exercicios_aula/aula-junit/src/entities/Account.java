package entities;

import java.security.PublicKey;

public class Account {

    private static Double DEPOSIT_FEE_PERCENTAGE = 0.98;

    private Long id;
    private Double balance;

    public Account(){}

    public Account(Long id, Double balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void deposit(double amount){
        balance += (amount * DEPOSIT_FEE_PERCENTAGE);
    }

    public void withdraw(double amount){
        if (balance >= amount) {
            balance -= amount;
        }
    }
}
